package com.pracownia.vanet.model;

import com.pracownia.vanet.algorithm.AntyBogus;
import com.pracownia.vanet.model.event.Event;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.point.NetworkPoint;
import com.pracownia.vanet.model.point.Point;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import com.pracownia.vanet.util.Logger;
import com.pracownia.vanet.view.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Vehicle extends NetworkPoint {

    /*------------------------ FIELDS REGION ------------------------*/
    private int id;
    private double trustLevel;
    private double currentX;
    private double currentY;
    private Route route;
    private int iterator;
    private double speed;
    private boolean direction = true; // True if from starting point to end point
    private List<StationaryNetworkPoint> connectedPoints = new ArrayList<>();

    private Date date;
    @Setter(AccessLevel.NONE)
    private Point previousCrossing;
    private boolean safe = true;

    /*------------------------ METHODS REGION ------------------------*/
    public Vehicle() {
        super();
        route = new Route();
        trustLevel = 0.5;
        currentLocation = new Point();
    }

    public Vehicle(Route route, int id, double range, double speed) {
        super();
        this.route = route;
        this.id = id;
        this.range = range;
        this.speed = speed + 0.001;
        trustLevel = 0.5;
        this.currentLocation = new Point(route.getStartPoint().getX(), route.getStartPoint()
                .getY());
    }

    public void setPreviousCrossing(Point previousCrossing) {
        this.previousCrossing = previousCrossing;
        this.setDate(new Date());
    }

    public void setNotSafe(String mssg) {
        if (this.safe == true) {
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            Logger.log("[" + timeStamp + "] Vehicle " + id + " : " + mssg);
            System.out.println("[" + timeStamp + "] Vehicle " + id + " : " + mssg);
            this.safe = false;
        }
    }

    @Override
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

        for (StationaryNetworkPoint s : map.getStationaryNetworkPoints()) {
            if (distance(this.currentLocation, s.getCurrentLocation()) < range) {
                if (!connectedPoints.contains(s)) {
                    connectedPoints.add(s);
                }
            } else {
                if (isPointInList(s, connectedPoints)) {
                    connectedPoints.remove(s);
                }
            }
        }
        if (!connectedPoints.isEmpty()) {
            for (Event event : encounteredEvents) {
                AntyBogus.addEvent(event, this);
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

                if (!flag && this.trustLevel >= 0.5) {
                    connectedVehicle.getCollectedEvents().add(event);
                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    Logger.log("[" + timeStamp + "] Event " + event.getId() + " shared from "
                            + "Vehicle " + this
                            .getId() + " to Vehicle " + connectedVehicle.getId());
                    System.out.println("[" + timeStamp + "] Event " + event.getId() + " shared "
                            + "from Vehicle " + this
                            .getId() + " to Vehicle " + connectedVehicle.getId());
                }
            }
        }

        for (NetworkPoint connectedPoint : connectedPoints) {
            for (Event event : collectedEvents) {
                flag = false;
                for (Event outEvent : connectedPoint.getCollectedEvents()) {
                    if (event.getId() == outEvent.getId()) {
                        flag = true;
                    }
                }

                if (!flag && this.trustLevel >= 0.5) {
                    connectedPoint.getCollectedEvents().add(event);
                    Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                    Logger.log("[" + timeStamp + "] Event " + event.getId() + " shared from "
                            + "Vehicle " + this
                            .getId() + " to Stationary");
                    System.out.println("[" + timeStamp + "] Event " + event.getId() + " shared "
                            + "from Vehicle " + this
                            .getId() + " to Stationary");
                }
            }
        }
    }

    @Override
    public void update(Map map) {
        updateConnectedPoints(map);
        sendEventsToConnectedPoints();

        double distanceToEndPoint = Math.sqrt(Math.pow(route.getEndPoint()
                .getX() - currentLocation.getX(), 2) +
                Math.pow(route.getEndPoint().getY() - currentLocation.getY(), 2));

        double cos = (route.getEndPoint().getX() - currentLocation.getX()) / distanceToEndPoint;
        double sin = (route.getEndPoint().getY() - currentLocation.getY()) / distanceToEndPoint;

        double distanceToStart;

        if (direction) {
            distanceToStart = Math.sqrt(Math.pow(currentLocation.getX() - route.getStartPoint()
                    .getX(), 2) +
                    Math.pow(currentLocation.getY() - route.getStartPoint().getY(), 2));
            currentLocation.setX(currentLocation.getX() + cos * speed);
            currentLocation.setY(currentLocation.getY() + sin * speed);
        } else {
            distanceToStart = Math.sqrt(Math.pow(currentLocation.getX() - route.getEndPoint()
                    .getX(), 2) +
                    Math.pow(currentLocation.getY() - route.getEndPoint().getY(), 2));

            currentLocation.setX(currentLocation.getX() - cos * speed);
            currentLocation.setY(currentLocation.getY() - sin * speed);
        }

        if (distanceToStart >= route.getDistance()) {
            direction = !direction;
        }

        //System.out.println(this.toString());
    }

    public boolean isPointInList(StationaryNetworkPoint point, List<StationaryNetworkPoint> list) {
        boolean result = false;
        for (StationaryNetworkPoint s : list) {
            if (s.getId() == point.getId()) {
                result = true;
            }
        }
        return result;
    }

    public void addFakeEvent(EventSource eventSource) {
        AntyBogus.addEvent(eventSource.getEvent(), this);
        this.getEncounteredEvents().add(eventSource.getEvent());
    }

    @Override
    public String toString() {
        return "ID:\t" + id + '\t' + "safe: " + safe;
    }
}
    