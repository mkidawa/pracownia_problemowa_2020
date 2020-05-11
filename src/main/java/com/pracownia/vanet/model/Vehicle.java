package com.pracownia.vanet.model;

import com.pracownia.vanet.algorithm.AntyBogus;
import com.pracownia.vanet.model.event.Event;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.event.EventType;
import com.pracownia.vanet.model.point.NetworkPoint;
import com.pracownia.vanet.model.point.Point;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import com.pracownia.vanet.util.Logger;
import com.pracownia.vanet.view.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.*;

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
    private int currentLane;
    private List<StationaryNetworkPoint> connectedPoints = new ArrayList<>();
    private List<HistoryPoint> log = new ArrayList<>();
    private Date date;
    @Setter(AccessLevel.NONE)
    private Point previousCrossing;
    private boolean tooFast;
    private boolean safe = true;
//    Position estimation
    protected Point previousLocation;
    private Direction whichWay = new Direction();
    private boolean inAccident;


    /*------------------------ METHODS REGION ------------------------*/
    public Vehicle(Route route, int id, double range, double speed, int currentLane) {
        super();
        this.route = route;
        this.id = id;
        this.range = range;
        this.speed = speed + 0.001;
        this.currentLane = currentLane;
        trustLevel = 0.5;
        tooFast = false;
        this.currentLocation = new Point(route.getStartPoint().getX(), route.getStartPoint()
                .getY());

        this.previousLocation = new Point(route.getStartPoint().getX(), route.getStartPoint()
                .getY());

    }

    public void setPreviousCrossing(Point previousCrossing) {
        this.previousCrossing = previousCrossing;
        this.setDate(new Date());
    }

    public void setNotSafe(String mssg) {
        if (this.safe == true) {
            Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
            String msg = "[" + timeStamp + "] Vehicle " + id + " : " + mssg;
            Logger.log(msg);
            System.out.println(msg);
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
                    String msg = "[" + timeStamp + "] Event " + event.getId()
                            + "["+event.getEventType().toString()+"]"+
                            " shared from " + "Vehicle " + this
                            .getId() + " to Vehicle " + connectedVehicle.getId();

                    Logger.log(msg);
                    System.out.println(msg);
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
                    String msg = "[" + timeStamp + "] Event " + event.getId()
                            +"["+event.getEventType().toString()+"]"
                            + " shared from " + "Vehicle " + this
                            .getId() + " to Stationary";

                    Logger.log(msg);
                    System.out.println(msg);
                }
            }
        }
    }

    public void changeDirection() {
        this.direction = !this.direction;
    }

    public void tryToChangeTrafficLane() {
        Random random = new Random();
        if (this.direction) {
            if (random.nextInt(100) < 1 && this.route.getNumOfTLTS() != 0) {
                this.currentLane = -1;
            } else if (this.route.getNumOfTLTE() != 0) {
                currentLane = random.nextInt(this.route.getNumOfTLTE())+1;
            }
        } else {
            if (random.nextInt(100) < 1 && this.route.getNumOfTLTE() != 0) {
                this.currentLane = -1;
            } else if (this.route.getNumOfTLTS() != 0)
                currentLane = random.nextInt(this.route.getNumOfTLTS())+1;
        }
        double multiplier = random.nextDouble() / 4;
        if (random.nextInt(10) < 1) {
            this.speed = route.getSpeedLimit() * (1 + multiplier);
        } else {
            this.speed = route.getSpeedLimit() * (1 - multiplier);
        }

    }

    public boolean isNewDirectionGood() {
        if ((this.direction && this.route.getNumOfTLTS() > 0) || (!this.direction && this.route.getNumOfTLTE() > 0)) {
            return true;
        }
        return false;
    }

    public double getDistanceToCrossing(Crossing crossing) {
        return Math.sqrt(Math.pow(crossing.getLocation().getX() - this.currentLocation.getX(), 2) +
                Math.pow(crossing.getLocation().getY() - this.currentLocation.getY(), 2));
    }

    @Override
    public void update(Map map) {
        log.add(new HistoryPoint(this.currentX, this.currentY, this.speed));
        while (log.size() > 30) {
            log.remove(0);
        }
        updateConnectedPoints(map);
        sendEventsToConnectedPoints();

        double distanceToEndPoint = Math.sqrt(Math.pow(route.getEndPoint()
                .getX() - currentLocation.getX(), 2) +
                Math.pow(route.getEndPoint().getY() - currentLocation.getY(), 2));
        if (distanceToEndPoint == 0) {
            distanceToEndPoint = 1; // disappearing vehicles (NaN value in point)
        }
        double cos = (route.getEndPoint().getX() - currentLocation.getX()) / distanceToEndPoint;
        double sin = (route.getEndPoint().getY() - currentLocation.getY()) / distanceToEndPoint;

        double distanceToStart;
        Random random = new Random();
        if (direction) {
            distanceToStart = Math.sqrt(Math.pow(currentLocation.getX() - route.getStartPoint()
                    .getX(), 2) +
                    Math.pow(currentLocation.getY() - route.getStartPoint().getY(), 2));
//            Set previous point
            previousLocation.setX(currentLocation.getX());
            previousLocation.setY(currentLocation.getY());
            currentLocation.setX(currentLocation.getX() + cos * speed);
            currentLocation.setY(currentLocation.getY() + sin * speed);
            if (route.getNumOfTLTE() == 0) {
                currentLane = -1;
            } else {
                int newLane = random.nextInt(route.getNumOfTLTE() * 100);
                if (newLane < route.getNumOfTLTE()) {
                    currentLane = newLane + 1;
                }
            }

        } else {
            distanceToStart = Math.sqrt(Math.pow(currentLocation.getX() - route.getEndPoint()
                    .getX(), 2) +
                    Math.pow(currentLocation.getY() - route.getEndPoint().getY(), 2));
            previousLocation.setX(currentLocation.getX());
            previousLocation.setY(currentLocation.getY());
            currentLocation.setX(currentLocation.getX() - cos * speed);
            currentLocation.setY(currentLocation.getY() - sin * speed);
            if (route.getNumOfTLTS() == 0) {
                currentLane = -1;
            } else {
                int newLane = random.nextInt(route.getNumOfTLTS() * 100);
                if (newLane < route.getNumOfTLTS()) {
                    currentLane = newLane + 1;
                }
            }
        }

        if (distanceToStart >= route.getDistance()) {
            direction = !direction;
        }

        if (speed > route.getSpeedLimit()) {
            tooFast = true;
            //System.out.println("ID: "+id+" is riding too fast");
        } else
            tooFast = false;
        //System.out.println(this.toString());
//        System.out.println("curr" + currentLocation.getX() + "|" +currentLocation.getY());
//        System.out.println("prev" + previousPoint.getX() + "|" +previousPoint.getY());
        if(currentLocation.getX()- previousLocation.getX() == 0) {
            if(currentLocation.getY()> previousLocation.getY()) {
//                System.out.println("JAZDA W DOL");
                whichWay.direction=DirectionEnum.DOWN;
            } else {
//                System.out.println("JAZDA W GORE");
                whichWay.direction=DirectionEnum.UP;
            }
        }
        if(currentLocation.getY()- previousLocation.getY() == 0) {
            if(currentLocation.getX()< previousLocation.getX()) {
//                System.out.println("JAZDA W LEWO");
                whichWay.direction=DirectionEnum.LEFT;
            } else {
//                System.out.println("JAZDA W PRAWO");
                whichWay.direction=DirectionEnum.RIGHT;
            }
        }
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
    public double getDistanceBetweenCar(Vehicle v) {
        double x1 = this.getCurrentLocation().getX();
        double y1 = this.getCurrentLocation().getY();
        double x2 = v.getCurrentLocation().getX();
        double y2 = v.getCurrentLocation().getY();
        return Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));
    }
    @Override
    public String toString() {
        return "ID:\t" + id + '\t' + "safe: " + safe;
    }
}
    