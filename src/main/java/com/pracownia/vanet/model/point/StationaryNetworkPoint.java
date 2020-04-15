package com.pracownia.vanet.model.point;

import com.pracownia.vanet.algorithm.AntyBogus;
import com.pracownia.vanet.model.RLUTag;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.util.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;

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

    public void checkForSybilVehicles() {
        for (Vehicle v1 : connectedVehicles) {
            if (!v1.isSafe()) continue;
            for (Vehicle v2 : connectedVehicles) {
                if (v1 == v2 || !v2.isSafe()) continue;
                if (areChainTagsSimilar(v1.getObtainedTags(), v2.getObtainedTags())) {
                    markAsSybil(v1);
                    markAsSybil(v2);
                }
            }
        }
    }

    private void markAsSybil(Vehicle v) {
        v.setNotSafe("Its a sybil");
    }

    private boolean areChainTagsSimilar(LinkedList<RLUTag> firstVehicleTags, LinkedList<RLUTag> secondVehicleTags) {
        int yeetVehicleTreshold = 3;
        int sameTagsCount = 0;
        if (firstVehicleTags.size() < yeetVehicleTreshold || secondVehicleTags.size() < yeetVehicleTreshold)
            return false;
        for (int i = 0; i < Math.min(firstVehicleTags.size(), secondVehicleTags.size()); i++) {
            if (firstVehicleTags.get(i).equals(secondVehicleTags.get(i))) {
                sameTagsCount++;
            }
        }
        if (sameTagsCount >= yeetVehicleTreshold)
            return true;
        return false;
    }

    public RLUTag obtainTag() {
        int roundedTimestamp = (int) new Date().getTime() / 100 * 100;
        return new RLUTag(id, roundedTimestamp);
    }

}
    