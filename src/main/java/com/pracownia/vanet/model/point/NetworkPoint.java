package com.pracownia.vanet.model.point;

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

@Getter
@Setter
@NoArgsConstructor
public abstract class NetworkPoint {

    /*------------------------ FIELDS REGION ------------------------*/
    protected int id;
    protected Point currentLocation = new Point();
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
        for (Vehicle v : map.getVehicles()) {
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
    