package com.pracownia.vanet.model;

import com.pracownia.vanet.view.Map;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SybilVehicle extends Vehicle {
    @Getter
    private final List<Vehicle> fakeVehicles = new ArrayList<Vehicle>();

    public SybilVehicle(Route route, int id, double range, double speed, int currentLane, int fakeCount)
    {
        super(route, id, range, speed, currentLane);
        for (int i = 0; i < fakeCount; i++) {
            fakeVehicles.add(new Vehicle(route, id + i + 1, range, speed, currentLane));
        }
    }

    @Override
    public void update(Map map) {
        super.update(map);
        for (Vehicle v : fakeVehicles) {
            v.update(map);
            v.currentLocation.setX(currentLocation.getX());
            v.currentLocation.setY(currentLocation.getY());
        }
    }

}
