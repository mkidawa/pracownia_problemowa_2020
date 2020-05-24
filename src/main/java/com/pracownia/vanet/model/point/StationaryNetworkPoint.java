package com.pracownia.vanet.model.point;

import com.pracownia.vanet.algorithm.AntyBogus;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.util.Logger;

import java.sql.Timestamp;

public class StationaryNetworkPoint extends NetworkPoint {

    /*------------------------ FIELDS REGION ------------------------*/
    private final static double TRUST_LEVEL_INCREASE = 0.1;
    private final static double TRUST_LEVEL_DECREASE = 0.4;

    /*------------------------ METHODS REGION ------------------------*/
    public StationaryNetworkPoint(int id, Point currentLocation, double range) {
        super(id, currentLocation, range);
    }

    private void increaseVehicleTrustLevel(Vehicle vehicle) {
        double previousTrustLevel = vehicle.getTrustLevel();
        vehicle.setTrustLevel(previousTrustLevel + TRUST_LEVEL_INCREASE);
    }

    private void decreaseVehicleTrustLevel(Vehicle vehicle) {
        double previousTrustLevel = vehicle.getTrustLevel();
        vehicle.setTrustLevel(previousTrustLevel - TRUST_LEVEL_DECREASE);
    }

    public void checkIfChangeVehicleTrustLevel() {
        for (Vehicle v : this.connectedVehicles) {
            if (AntyBogus.getVehiclesToIncreaseTrustLevel().contains(v)) {

                increaseVehicleTrustLevel(v);
                AntyBogus.getVehiclesToIncreaseTrustLevel().remove(v);
                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Logger.log("[" + timeStamp + "] Increased trust level of Vehicle " + v.getId());
                System.out.println("[" + timeStamp + "] Increased trust level of Vehicle " + v.getId());

            } else if (AntyBogus.getVehiclesToDecreaseTrustLevel().contains(v)) {

                decreaseVehicleTrustLevel(v);
                AntyBogus.getVehiclesToDecreaseTrustLevel().remove(v);
                Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                Logger.log("[" + timeStamp + "] Decreased trust level of Vehicle " + v.getId());
                System.out.println("[" + timeStamp + "] Decreased trust level of Vehicle " + v.getId());

            }
        }
    }
}
    