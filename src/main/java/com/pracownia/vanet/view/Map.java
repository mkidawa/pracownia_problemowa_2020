package com.pracownia.vanet.view;

import com.pracownia.vanet.model.Crossing;
import com.pracownia.vanet.model.Route;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.event.EventType;
import com.pracownia.vanet.model.point.Point;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Map {

    /*------------------------ FIELDS REGION ------------------------*/
    private double width = 1000.0;
    private double height = 900.0;

    private static int fakeCarId = -666;
    private static int fakeEventId = -1;

    private List<Route> routes;
    private ObservableList<Vehicle> vehicles;
    private List<Crossing> crossings;
    private List<EventSource> eventSources;
    private List<StationaryNetworkPoint> stationaryNetworkPoints;
    private ObservableList<Vehicle> hackers;

    /*------------------------ METHODS REGION ------------------------*/
    public Map() {

        routes = new ArrayList<>();
        vehicles = FXCollections.observableArrayList();

        //		vehicles.addListener((ListChangeListener.Change<? extends Vehicle> change) -> {
        //			while (change.next()) {
        //				for (Vehicle vehicle : vehicles) {
        //					if(!vehicle.safe){
        //						if(!hackers.contains(vehicle))
        //							hackers.add(vehicle);
        //					}else{
        //						if(hackers.contains(vehicle))
        //							hackers.remove(vehicle);
        //					}
        //				}
        ////				hackersvehicles.stream().filter(x->!x.safe).collect(Collectors.toList
        // ()));
        //			}
        //		});

        hackers = FXCollections.observableArrayList();
        crossings = new ArrayList<>();
        eventSources = FXCollections.observableArrayList();
        stationaryNetworkPoints = new ArrayList<>();
        initMap();

    }

    private void initMap() {
        routes.add(new Route(200.0, 75.0, 200.0, 725.0, 4,2,1));
        routes.add(new Route(400.0, 75.0, 400.0, 725.0, 4,3,2));
        routes.add(new Route(600.0, 75.0, 600.0, 725.0, 4,2,2));
        routes.add(new Route(800.0, 75.0, 800.0, 725.0, 4,1,3));
        routes.add(new Route(75.0, 200.0, 925.0, 200.0, 4,1,2));
        routes.add(new Route(200.0, 400.0, 800.0, 400.0, 4,3,0));
        routes.add(new Route(75.0, 600.0, 925.0, 600.0, 4,3,3));

        crossings.add(new Crossing(new Point(200.0, 200.0), routes.get(0), routes.get(4)));
        crossings.add(new Crossing(new Point(200.0, 400.0), routes.get(0), routes.get(5)));
        crossings.add(new Crossing(new Point(200.0, 600.0), routes.get(0), routes.get(6)));
        crossings.add(new Crossing(new Point(400.0, 200.0), routes.get(1), routes.get(4)));
        crossings.add(new Crossing(new Point(400.0, 400.0), routes.get(1), routes.get(5)));
        crossings.add(new Crossing(new Point(400.0, 600.0), routes.get(1), routes.get(6)));
        crossings.add(new Crossing(new Point(600.0, 200.0), routes.get(2), routes.get(4)));
        crossings.add(new Crossing(new Point(600.0, 400.0), routes.get(2), routes.get(5)));
        crossings.add(new Crossing(new Point(600.0, 600.0), routes.get(2), routes.get(6)));
        crossings.add(new Crossing(new Point(800.0, 200.0), routes.get(3), routes.get(4)));
        crossings.add(new Crossing(new Point(800.0, 400.0), routes.get(3), routes.get(5)));
        crossings.add(new Crossing(new Point(800.0, 600.0), routes.get(3), routes.get(6)));

        stationaryNetworkPoints.add(new StationaryNetworkPoint(0, new Point(200.0, 200.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(1, new Point(200.0, 400.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(3, new Point(200.0, 600.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(4, new Point(400.0, 200.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(5, new Point(400.0, 400.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(6, new Point(400.0, 600.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(7, new Point(600.0, 200.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(8, new Point(600.0, 400.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(9, new Point(600.0, 600.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(10, new Point(800.0, 200.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(11, new Point(800.0, 400.0), 110.0));
        stationaryNetworkPoints.add(new StationaryNetworkPoint(12, new Point(800.0, 600.0), 110.0));

        eventSources.add(new EventSource(0, "Car Accident", "Serious Car Accident",
                new Point(250.0, 210.0), new Date(), 30.0, EventType.CAR_ACCIDENT));
//
//        eventSources.add(new EventSource(1, "TRAFFIC_JAM", "Serious TRAFFIC_JAM",
//                new Point(500.0, 410.0), new Date(), 30.0, EventType.TRAFFIC_JAM));
//
//        eventSources.add(new EventSource(2, "POLICE_CONTROL", "Serious POLICE_CONTROL",
//                new Point(750.0, 610.0), new Date(), 30.0, EventType.POLICE_CONTROL));
    }

    public List<Integer> deleteUnsafeVehicles() {
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).isSafe() == false) {
                result.add(i);
                vehicles.remove(i);
                i--;
            }
        }

        return result;
    }

    public void addVehicles(int amount) {
        Random random = new Random();
        int numOfVehicles = vehicles.size();
        for (int i = numOfVehicles; i < amount + numOfVehicles; i++) {
            vehicles.add(new Vehicle(
                    routes.get(i % 5),
                    i,
                    40.0,
                    random.nextDouble() * 4.0 + 2,
                    random.nextInt(routes.get(i%5).getNumOfTLTE())+1));
        }
    }

    public void changeVehiclesSpeed(double value) {
        for(Vehicle v : vehicles){
            v.setChangedSpeed(value);
        }
    }

    public Vehicle addCopy() {
        if (vehicles.size() == 0) {
            throw new IllegalArgumentException();
        }
        int r = new Random().nextInt(vehicles.size());
        Vehicle me = new Vehicle(vehicles.get(r).getRoute(), vehicles.get(r)
                .getId(), vehicles.get(r).getRange(), vehicles.get(r).getSpeed(), vehicles.get(r).getCurrentLane());
        vehicles.add(me);

        return me;
    }

    public void addFakeVehicle(String nameEvent) {
        Random random = new Random();
        int x = (int) (random.nextDouble() * 1000);
        int y = (int) (random.nextDouble() * 1000);
        Vehicle vehicle = new Vehicle(routes.get(99 % 5), fakeCarId, 40.0,
                random.nextDouble() / 2.0 + 2, 1);
        EventSource eventSource = new EventSource(fakeEventId, nameEvent, "Fake Car Accident",
                new Point(x, y), new Date(), 20.0, EventType.CAR_ACCIDENT);
        vehicle.addFakeEvent(eventSource);
        vehicles.add(vehicle);
        fakeCarId--;
        fakeEventId--;
    }

    public void logCrossingHackerCount() {
        crossings.forEach(Crossing::logHackerCount);
    }
}
    