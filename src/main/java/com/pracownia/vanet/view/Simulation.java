package com.pracownia.vanet.view;

import com.pracownia.vanet.model.Crossing;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.Event;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import com.pracownia.vanet.util.Logger;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private List<Label> labelList;
    private List<Circle> stationaryCirclelist;

    /*------------------------ METHODS REGION ------------------------*/
    public Simulation() {
        map = new Map();
        circleList = new ArrayList<>();
        rangeList = new ArrayList<>();
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

                //showVehiclesConnected();
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

                if (vehicle.isSafe() != true) {
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
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            it++;
        }
    }

    private void checkVehicleCrossing() {
        for (Vehicle vehicle : map.getVehicles()) {
            for (Crossing crossing : map.getCrossings()) {

                if (crossing.getDistanceToCrossing(vehicle) < Crossing.DETECTION_RANGE) {
                    crossing.transportVehicle(vehicle);

                }
            }
        }
    }

    private void resetReferences() {
        for (Crossing crossing : map.getCrossings()) {
            crossing.resetLastTransportedVehicle();
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

                    vehicle.getCollectedEvents().add(eventSource.getEvent());
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
                    Logger.log("[" + timeStamp + "] Event " + eventSource.getId() + " encountered"
                            + " by Vehicle " + vehicle
                            .getId());
                    System.out.println("[" + timeStamp + "] Event " + eventSource.getId() + " "
                            + "encountered by Vehicle " + vehicle
                            .getId());
                }
            }
        }
    }

    private void updateStationaryPoints() {
        int it = 0;
        for (StationaryNetworkPoint s : map.getStationaryNetworkPoints()) {
            s.update(map);
            s.checkIfChangeVehicleTrustLevel();
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
    }

    public void switchOnRangeCircles() {
        for (Circle rangeCircle : rangeList) {
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

        if (map.getVehicles().size() < 0) {
            return;
        }
        Vehicle vehicle = map.getVehicles().get(new Random().nextInt(map.getVehicles().size()));

        vehicle.setCurrentLocation(map.getCrossings()
                .get(new Random().nextInt(map.getCrossings().size()))
                .getLocation());
    }

    public void addHacker() {

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
    