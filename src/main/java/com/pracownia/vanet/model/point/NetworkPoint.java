package com.pracownia.vanet.model.point;

import com.pracownia.vanet.model.SybilVehicle;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.Event;
import com.pracownia.vanet.util.Logger;
import com.pracownia.vanet.view.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public abstract class NetworkPoint {

    /*------------------------ FIELDS REGION ------------------------*/
    protected int id;
    public Point currentLocation = new Point();
    protected double range;
    protected List<Vehicle> connectedVehicles = new ArrayList<>();
    protected List<Event> collectedEvents = new ArrayList<>();
    protected List<Event> encounteredEvents = new ArrayList<>();

    /*------------------------ METHODS REGION ------------------------*/
    protected double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    public NetworkPoint(int id, Point currentLocation, double range) {
        this.id = id;
        this.currentLocation = currentLocation;
        this.range = range;
    }

    public void updateConnectedPoints(Map map) {
        List<Vehicle> vehiclesToCheck = new ArrayList<>(map.getVehicles());

        List<Vehicle> fakes = vehiclesToCheck.stream()
                .filter(v -> v instanceof SybilVehicle)
                .map(v -> ((SybilVehicle)v).getFakeVehicles())
                .flatMap(List::stream)
                .collect(Collectors.toList());
        vehiclesToCheck.addAll(fakes);

        for (Vehicle v : vehiclesToCheck) {
            if (v == this) {
                continue;
            }

            if (distance(this.currentLocation, v.currentLocation) < range) {
                if (!connectedVehicles.contains(v)) {
                    connectedVehicles.add(v);
                }
            } else if (connectedVehicles.contains(v)) {
                connectedVehicles.remove(v);
            }
        }
    }

    public void sendEventsToConnectedPoints() {
        boolean flag;

        for (NetworkPoint connectedVehicle : connectedVehicles) {
            for (Event event : collectedEvents) {
                flag = false;
                for (Event outEvent : connectedVehicle.getCollectedEvents()) {
                    if (event.getId() == outEvent.getId()) {
                        flag = true;
                    }
                }

                if (!flag) {
                    connectedVehicle.getCollectedEvents().add(event);
                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    Logger.log("[" + timeStamp + "] Event " + event.getId() + " shared from "
                            + "Stationary to Vehicle " + connectedVehicle
                            .getId());
                    System.out.println("[" + timeStamp + "] Event " + event.getId() + " shared "
                            + "from Stationary to Vehicle " + connectedVehicle
                            .getId());
                }
            }
        }
    }

    public void update(Map map) {
        updateConnectedPoints(map);
        sendEventsToConnectedPoints();
    }
}
    