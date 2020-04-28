package com.pracownia.vanet.model;

import com.pracownia.vanet.model.point.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
public class Crossing {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final double DETECTION_RANGE = 3.0;

    private Point location;
    private Route routeA;
    private Route routeB;
    private List<Vehicle> vehicles = new ArrayList<>();

    /*------------------------ METHODS REGION ------------------------*/
    public Crossing(Point location, Route routeA, Route routeB) {
        this.location = location;
        this.routeA = routeA;
        this.routeB = routeB;
    }

    private void changeRoute(Vehicle vehicle) {
        if (vehicle.getRoute() == routeA) {
            vehicle.setRoute(routeB);
        } else {
            vehicle.setRoute(routeA);
        }
    }

    public void transportVehicle(Vehicle vehicle) {
        if (vehicle.getCurrentLane() == -1) {
            this.changeRoute(vehicle);
            if (vehicle.isNewDirectionGood()) {
                vehicles.add(vehicle);
                vehicle.changeDirection();
                vehicle.tryToChangeTrafficLane();
            }
            return;
        }

        if (vehicles.contains(vehicle)) {
            return;
        }

        vehicles.add(vehicle);

        Random random = new Random();
        int pom = random.nextInt();

        if (Math.abs(pom % 3) == 0 || Math.abs(pom % 3) == 1 || vehicle.getCurrentLane() == -1) {
            if (vehicle.getRoute() == routeA) {
                vehicle.setRoute(routeB);
            } else {
                vehicle.setRoute(routeA);
            }

            vehicle.setCurrentLocation(new Point(location.getX(), location.getY()));

            if (Math.abs(pom % 3) == 0) {
                if (vehicle.isNewDirectionGood())
                    vehicle.changeDirection();
            }
        }

        vehicle.tryToChangeTrafficLane();

        if (vehicle.getPreviousCrossing() != null && vehicle.getPreviousCrossing() != this.location) {
            double s = Math.sqrt(Math.pow((location.getX() - vehicle.getPreviousCrossing()
                    .getX()), 2) + Math.pow(location.getY() - vehicle.getPreviousCrossing()
                    .getY(), 2));
            double t = Math.abs(new Date().getTime() - vehicle.getDate().getTime());

            double v = s / (t / 50);
            //            System.out.println("Szybkosc auta: " + vehicle.getSpeed());
            //            System.out.println("Wyliczona: " + v);
            //            System.out.println("Czas: " + t);
            //            System.out.println("droga: " + s);

            //            System.out.println("pozycja auta: " + vehicle.getPreviousCrossing()
            //            .getX() + " --- " +vehicle.getPreviousCrossing().getY() );
            //            System.out.println("pozycja skrzyzowania: " + location.getX() + " --- "
            //            + location.getY());

            if (Math.abs(v - vehicle.getSpeed()) > 0.5) {
                vehicle.setNotSafe("Identified as attacker!");
            } else if (vehicle.getTrustLevel() < 0.3) {
                vehicle.setNotSafe("Identified as attacker!");
            } else {
                //System.out.println("Bezpiecznie.");
            }
        }

        vehicle.setPreviousCrossing(location);
    }

    public void refreshVehicles() {
        vehicles.removeIf(vehicle -> vehicle.getDistanceToCrossing(this) > Crossing.DETECTION_RANGE);
    }
}
    