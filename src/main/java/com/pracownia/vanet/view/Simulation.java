package com.pracownia.vanet.view;

import com.pracownia.vanet.algorithm.AntyBogus;
import com.pracownia.vanet.model.Crossing;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.Event;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.event.EventType;
import com.pracownia.vanet.model.point.Point;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import com.pracownia.vanet.util.Logger;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.pracownia.vanet.util.Utils.setTimeout;

@Getter
@Setter
public class Simulation implements Runnable {

    /*------------------------ FIELDS REGION ------------------------*/
    private Color here = Color.RED;
    private Boolean simulationRunning;
    private Thread tr;
    private Map map;
    private List<Circle> circleList;
    private List<Circle> rangeList;
    private List<Circle> rangeRsuList;
    private List<Label> labelList;
    private List<Circle> stationaryCirclelist;
    private boolean accidents = true;
    private static double DISTANCE_FOR_CRASH = 10.0;

    /*------------------------ METHODS REGION ------------------------*/
    public Simulation() {
        map = new Map();
        circleList = new ArrayList<>();
        rangeList = new ArrayList<>();
        rangeRsuList = new ArrayList<>();
        labelList = new ArrayList<>();
        stationaryCirclelist = new ArrayList<>();
        this.simulationRunning = false;
        tr = new Thread(this);
    }

    @Override
    public void run() {
        while (true) {
            if (simulationRunning) {
                updateVehiclesPosition();
                checkVehicleCrossing();
                resetReferences();
                checkVehicleEventSource();
                updateStationaryPoints();
                checkCopies();
                if (accidents) {
                    checkAccident();
                }
                //showVehiclesConnected();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public Map getMap() {
        return map;
    }


    private void updateVehiclesPosition() {
        int it = 0;
        for (Vehicle vehicle : map.getVehicles()) {
            vehicle.update(map);
            try {
                double vehicleX = vehicle.getCurrentLocation().getX();
                double vehicleY = vehicle.getCurrentLocation().getY();
                circleList.get(it).setCenterX(vehicleX);
                circleList.get(it).setCenterY(vehicleY);

                rangeList.get(it).setCenterX(vehicleX);
                rangeList.get(it).setCenterY(vehicleY);

                if (!vehicle.isSafe()) {
                    circleList.get(it).setFill(here);
                    //labelList.get(it).setText(String.valueOf(vehicle.getCollectedEvents().size
                    // ()));

                    if (vehicle.getTrustLevel() < 0.3) {
                        circleList.get(it).setFill(here);
                        //                    } else if (vehicle.getCollectedEvents().size() > 0) {
                        //                        circleList.get(it).setFill(Color.BROWN);
                        //                    }

                        labelList.get(it).setLayoutX(vehicleX + 7.0);
                        labelList.get(it).setLayoutY(vehicleY);
                    }
                }

                if (vehicle.getCurrentLane() == -1) {
                    circleList.get(it).setFill(Color.BLACK);
                }

                if (vehicle.getCurrentLane() == 1) {
                    circleList.get(it).setFill(Color.AQUA);
                }

                if (vehicle.getCurrentLane() == 2) {
                    circleList.get(it).setFill(Color.GOLD);
                }

                if (vehicle.getCurrentLane() == 3) {
                    circleList.get(it).setFill(Color.CORAL);
                }

                if (vehicle.isTooFast()) {
                    circleList.get(it).setFill(Color.DARKRED);
                }

            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            it++;
        }
    }

    private void checkVehicleCrossing() {
        for (Vehicle vehicle : map.getVehicles()) {
            for (Crossing crossing : map.getCrossings()) {
                if (vehicle.getDistanceToCrossing(crossing) < Crossing.DETECTION_RANGE) {
                    crossing.transportVehicle(vehicle);
                }
            }
        }
    }

    private void resetReferences() {
        for (Crossing crossing : map.getCrossings()) {
            crossing.refreshVehicles();
        }
    }

    private void checkVehicleEventSource() {
        checkVehicleEventSourceEncountered();
        checkVehicleEventSourceCollected();
    }

    private void checkVehicleEventSourceCollected() {
        for (Vehicle vehicle : map.getVehicles()) {
            for (EventSource eventSource : map.getEventSources()) {
                if (eventSource.isInRange(vehicle.getCurrentLocation())) {
                    for (Event event : vehicle.getCollectedEvents()) {
                        if (event.getId() == eventSource.getId()) {
                            return;
                        }
                    }
                    if (!AntyBogus.analieseVehicle(vehicle, eventSource.getEvent())) {
                        vehicle.getCollectedEvents().add(eventSource.getEvent());
                    }

                }
            }
        }
    }

    private void checkVehicleEventSourceEncountered() {
        for (Vehicle vehicle : map.getVehicles()) {
            for (EventSource eventSource : map.getEventSources()) {
                if (eventSource.isInRange(vehicle.getCurrentLocation())) {
                    for (Event event : vehicle.getEncounteredEvents()) {
                        if (event.getId() == eventSource.getId()) {
                            return;
                        }
                    }

                    vehicle.getEncounteredEvents().add(eventSource.getEvent());
                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    String msg = "[" + timeStamp + "] Event " + eventSource.getId() +
                            "[" + eventSource.getName() + "]"
                            + " encountered" + " by Vehicle " + vehicle.getId();
                    Logger.log(msg);
                    System.out.println(msg);
                }
            }
        }
    }

    private void updateStationaryPoints() {
        int it = 0;
        for (StationaryNetworkPoint s : map.getStationaryNetworkPoints()) {
            s.update(map);
            s.checkIfChangeVehicleTrustLevel();
            s.checkForSybilVehicles();
            try {
                if (s.getConnectedVehicles().size() > 0) {
                    stationaryCirclelist.get(it).setFill(Color.ORANGE);
                } else {
                    stationaryCirclelist.get(it).setFill(Color.BLUE);
                }
                //if (s.getCollectedEvents().size() > 0) { stationaryCirclelist.get(it).setFill
                // (Color.CYAN); }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            it++;
        }
    }

    public void switchOffRangeCircles() {
        for (Circle rangeCircle : rangeList) {
            rangeCircle.setStroke(Color.TRANSPARENT);
        }
        for (Circle rangeCircle : rangeRsuList) {
            rangeCircle.setStroke(Color.TRANSPARENT);
        }
    }

    public void switchOnRangeCircles() {
        for (Circle rangeCircle : rangeList) {
            rangeCircle.setStroke(Color.BLACK);
        }
        for (Circle rangeCircle : rangeRsuList) {
            rangeCircle.setStroke(Color.BLACK);
        }
    }

    public void changeVehiclesRanges(double range) {
        for (Vehicle vehicle : map.getVehicles()) {
            vehicle.setRange(range);
        }
        for (Circle rangeCircle : rangeList) {
            rangeCircle.setRadius(range);
        }
    }

    public void teleportVehicle() {

        if (map.getVehicles().size() == 0) {
            Logger.log("Nothing to teleport");
            System.out.println("Nothing to teleport");
            return;
        }
        Vehicle vehicle = map.getVehicles().get(new Random().nextInt(map.getVehicles().size()));

        vehicle.setCurrentLocation(map.getCrossings()
                .get(new Random().nextInt(map.getCrossings().size()))
                .getLocation());
    }

    public void addHacker() {

    }

    public void checkAccident() {
        int size = map.getVehicles().size();
        for (int i = 0; i < map.getVehicles().size(); i++) {
            for (int j = i + 1; j < map.getVehicles().size(); j++) {
                Vehicle a = map.getVehicles().get(i);
                Vehicle b = map.getVehicles().get(j);
//                System.out.println(a.getCurrentLocation().toString());
//                System.out.println(b.getCurrentLocation().toString());
                double distanceBetweenCars = a.getDistanceBetweenCar(b);
//                System.out.println(distanceBetweenCars);
                if (distanceBetweenCars < DISTANCE_FOR_CRASH) {
                    if (a.getWhichWay().direction == b.getWhichWay().getOpposite() && (a.getLane() == b.getLane() || a.getLane() == -1 || b.getLane() == -1))
                        if (!a.isInAccident() || !b.isInAccident()) {
                            a.setInAccident(true);
                            b.setInAccident(true);
                            a.setSpeed(0);
                            b.setSpeed(0);
//                            setTimeout(() ->
//                            {
//                                a.setDefaultSpeed();
//                                b.setDefaultSpeed();
//                            }, 1000);
//                            setTimeout(() -> {
//                                a.setInAccident(false);
//                                b.setInAccident(false);
//                            }, 10000);
                            String msg = "Vehicle " + a.getId() +
                                    " crashed with Vehicle " + b.getId() +
                                    "on pos [" + a.getCurrentLocation().getX() + "," + a.getCurrentLocation().getY() + "]";
                            System.out.println(msg);

                            int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
                            map.getEventSources().add(new EventSource(
                                    uniqueId,
                                    "Car Accident",
                                    "Serious Car Accident",
                                    new Point(a.getCurrentLocation().getX(), a.getCurrentLocation().getY()),
                                    new Date(),
                                    15.0,
                                    EventType.CAR_ACCIDENT));

//                            setTimeout(() -> {
//                                map.getEventSources().remove(uniqueId);
//                            }, 10000);

                            System.out.println(map.getEventSources().toString());
                        }
//
                }
            }
        }
    }

    public void checkCopies() {
        int size = map.getVehicles().size();
        for (int i = 0; i < map.getVehicles().size(); i++) {
            for (int j = i + 1; j < map.getVehicles().size(); j++) {
                if (map.getVehicles().get(i).getId() == map.getVehicles().get(j).getId()) {
                    map.getVehicles().get(j).setNotSafe("KLON");
                    map.getVehicles().get(i).setNotSafe("KLON");
                    System.out.println(map.getVehicles()
                            .get(i)
                            .getId() + " ... " + map.getVehicles().get(j).getId());
                }
            }
        }
    }

    public void deleteUnsafeCircles() {
        List which = map.deleteUnsafeVehicles();
        //        for(int i = 0; i < which.size(); i++) {
        //            circleList.remove(which.get(i));
        //            rangeList.remove(which.get(i));
        //            i--;
        //        }
    }

    public void logCrossingHackerCount() {
        map.logCrossingHackerCount();
    }
/*
	private void showVehiclesConnected(){
		int it = 0;
		for (Vehicle vehicle : map.getVehicles()) {
			for (Vehicle vehicle2 : map.getVehicles()) {
				if(vehicle.id != vehicle2.id){
					double xcoord = Math.abs (vehicle.getCurrentX() - vehicle2.getCurrentX());
					double ycoord = Math.abs (vehicle.getCurrentY() - vehicle2.getCurrentY());
					double distance = Math.sqrt(ycoord*ycoord + xcoord * xcoord);
					if(distance<=vehicle.getRange()) {
						circleList.get(it).setFill(Color.GREEN);
						break;
					}
					else circleList.get(it).setFill(Color.BLACK);
				}
			}
			it++;
		}
	}*/
}
    