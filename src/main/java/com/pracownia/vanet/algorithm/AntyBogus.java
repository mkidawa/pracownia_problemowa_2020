package com.pracownia.vanet.algorithm;

import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.Event;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
public class AntyBogus {

    /*------------------------ FIELDS REGION ------------------------*/
    private static final int CONFIRMATION_LEVEL = 2;
    private static final double TRUST_LEVEL_BONUS = 1.0;
    private static final double THRESH_HOLD = 10E-15;

    private static ConcurrentMap<Event, ObservableList<Vehicle>> eventsByVehicle;
    private static ConcurrentMap<Event, List<Vehicle>> modifiedTrustLevelVehicles;
    private static ScheduledExecutorService cleanEventsTaskExecutor;
    private static List<Vehicle> vehiclesToIncreaseTrustLevel;
    private static List<Vehicle> vehiclesToDecreaseTrustLevel;

    /*------------------------ METHODS REGION ------------------------*/
    static {
        eventsByVehicle = new ConcurrentHashMap<>();
        modifiedTrustLevelVehicles = new ConcurrentHashMap<>();
        vehiclesToIncreaseTrustLevel = new ArrayList<>();
        vehiclesToDecreaseTrustLevel = new ArrayList<>();

        cleanEventsTaskExecutor = Executors.newScheduledThreadPool(1);
        cleanEventsTaskExecutor.scheduleAtFixedRate(createCleaningEventsTask(),
                30,
                30,
                TimeUnit.SECONDS
        );
    }

    public static List<Vehicle> getVehiclesToIncreaseTrustLevel() {
        return vehiclesToIncreaseTrustLevel;
    }

    public static List<Vehicle> getVehiclesToDecreaseTrustLevel() {
        return vehiclesToDecreaseTrustLevel;
    }

    private static Runnable createCleaningEventsTask() {
        return () -> {
            Date currentDate = new Date(System.currentTimeMillis());
            for (Event e : eventsByVehicle.keySet()) {
                if (currentDate.getTime() >= (e.getEventDate()
                        .getTime() + TimeUnit.SECONDS.toMillis(15))) {
                    if (eventsByVehicle.get(e).size() < CONFIRMATION_LEVEL) {
                        addVehicleToDecrease(eventsByVehicle.get(e), e);
                        eventsByVehicle.remove(e);
                    }
                }
            }
        };
    }

    private static ObservableList<Vehicle> createObservableList(
            final Vehicle vehicle, final Event event) {

        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
        vehicles.add(vehicle);

        vehicles.addListener((ListChangeListener<Vehicle>) c -> {
            checkIfEnoughConfirmations(c.getList(), event);
        });

        return vehicles;
    }

    private static void checkIfEnoughConfirmations(ObservableList<? extends Vehicle> list,
                                                   Event event) {
        if (list.size() >= CONFIRMATION_LEVEL || checkIfEventConfirmedByTrustedVehicle(list)) {
            addVehicleToIncrease(list, event);
        }
    }

    private static boolean checkIfEventConfirmedByTrustedVehicle(
            ObservableList<? extends Vehicle> list) {
        for (Vehicle v : list) {
            if (Math.abs(v.getTrustLevel() - 100.0) < THRESH_HOLD) {
                return true;
            }
        }

        return false;
    }

    private synchronized static void addVehicleToIncrease(
            ObservableList<? extends Vehicle> vehicleList, Event event) {
        for (Vehicle v : vehicleList) {
            if (!vehiclesToIncreaseTrustLevel.contains(v)) {
                if (!modifiedTrustLevelVehicles.get(event).contains(v)) {
                    vehiclesToIncreaseTrustLevel.add(v);
                    modifiedTrustLevelVehicles.get(event).add(v);
                }
            }
        }
    }

    private synchronized static void addVehicleToDecrease(
            ObservableList<? extends Vehicle> vehicleList, Event event) {
        for (Vehicle v : vehicleList) {
            if (!vehiclesToDecreaseTrustLevel.contains(v)) {
                if (!modifiedTrustLevelVehicles.get(event).contains(v)) {
                    vehiclesToDecreaseTrustLevel.add(v);
                    modifiedTrustLevelVehicles.get(event).add(v);
                }
            }
        }
    }

    public static void addEvent(Event event, Vehicle vehicle) {
        if (!eventsByVehicle.containsKey(event)) {
            eventsByVehicle.put(event, createObservableList(vehicle, event));
            if (!modifiedTrustLevelVehicles.containsKey(event)) {
                modifiedTrustLevelVehicles.put(event, new ArrayList<>());
            }
        } else if (!eventsByVehicle.get(event).contains(vehicle)) {
            eventsByVehicle.get(event).add(vehicle);
        }
    }
}
    