package com.pracownia.vanet.view;

import com.pracownia.vanet.Main;
import com.pracownia.vanet.model.Route;
import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.model.event.EventSource;
import com.pracownia.vanet.model.point.NetworkPoint;
import com.pracownia.vanet.model.point.StationaryNetworkPoint;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class ShapesCreator {

    /*------------------------ FIELDS REGION ------------------------*/
    private Group root;
    private Simulation simulation;
    private Main main;

    /*------------------------ METHODS REGION ------------------------*/
    public ShapesCreator(Group root, Simulation simulation, Main main) {
        this.root = root;
        this.simulation = simulation;
        this.main = main;
    }

    private Circle circleCreator(Vehicle vehicle) {
        Circle circle = new Circle();
        circle.setCenterX(vehicle.getCurrentLocation().getX());
        circle.setCenterY(vehicle.getCurrentLocation().getY());
        circle.setFill(Color.BLACK);
        circle.setRadius(8.0);
        circle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            main.getSpeedField().setText(String.valueOf(vehicle.getSpeed()));
            main.getTrustLevelField().setText(String.valueOf(vehicle.getTrustLevel()));
            main.getVehIdField().setText(String.valueOf(vehicle.getId()));
            main.getConnPointsField().setText(String.valueOf(vehicle.getConnectedPoints().size()));
            main.getConnVehField().setText(String.valueOf(vehicle.getConnectedVehicles().size()));
            main.getConnEventsField().setText(String.valueOf(vehicle.getCollectedEvents().size()));
        });
        return circle;
    }

    private Circle circleCreator(EventSource eventSource) {
        Circle circle = new Circle();
        circle.setCenterX(eventSource.getLocalization().getX());
        circle.setCenterY(eventSource.getLocalization().getY());
        circle.setFill(Color.RED);
        circle.setRadius(8.0);
        return circle;
    }

    private Circle circleCreator(StationaryNetworkPoint stationaryNetworkPoint) {
        Circle circle = new Circle();
        circle.setCenterX(stationaryNetworkPoint.getCurrentLocation().getX());
        circle.setCenterY(stationaryNetworkPoint.getCurrentLocation().getY());
        circle.setFill(Color.BLUE);
        circle.setRadius(6.0);
        return circle;
    }

    private Circle rangeCreator(Vehicle vehicle) {
        Circle circle = new Circle();
        circle.setRadius(vehicle.getRange());
        circle.setCenterX(vehicle.getCurrentLocation().getX());
        circle.setCenterY(vehicle.getCurrentLocation().getY());
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.TRANSPARENT);
        return circle;
    }

    private Circle rangeCreator(EventSource eventSource) {
        Circle circle = new Circle();
        circle.setRadius(eventSource.getRange());
        circle.setCenterX(eventSource.getLocalization().getX());
        circle.setCenterY(eventSource.getLocalization().getY());
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.TRANSPARENT);
        return circle;
    }

    private Line lineCrator(Route route) {
        Line line = new Line();
        line.setStartX(route.getStartPoint().getX());
        line.setStartY(route.getStartPoint().getY());
        line.setEndX(route.getEndPoint().getX());
        line.setEndY(route.getEndPoint().getY());

        return line;
    }

    private Label labelCreator(NetworkPoint networkPoint) {
        Label label = new Label();
        label.setText(String.valueOf(networkPoint.getCollectedEvents().size()));
        label.setLayoutX(networkPoint.getCurrentLocation().getX());
        label.setLayoutY(networkPoint.getCurrentLocation().getY());

        return label;
    }

    public void setRoutesLines(Simulation simulation) {
        for (int i = 0; i < simulation.getMap().getRoutes().size(); i++) {
            Line line = lineCrator(simulation.getMap().getRoutes().get(i));
            root.getChildren().add(line);
        }
    }

    public void setVehicleCircles(Simulation simulation, int amount) {
        for (int i = simulation.getMap().getVehicles().size() - amount; i < simulation.getMap()
                .getVehicles()
                .size(); i++) {
            Circle circle = circleCreator(simulation.getMap().getVehicles().get(i));
            Circle rangeCircle = rangeCreator(simulation.getMap().getVehicles().get(i));
            simulation.getCircleList().add(circle);
            simulation.getRangeList().add(rangeCircle);
            root.getChildren().add(rangeCircle);
            root.getChildren().add(circle);
        }
    }

    public void setCopyCircle(Vehicle vehicle) {
        Circle circle = circleCreator(vehicle);
        Circle rangeCircle = rangeCreator(vehicle);
        simulation.getCircleList().add(circle);
        simulation.getRangeList().add(rangeCircle);
        root.getChildren().add(rangeCircle);
        root.getChildren().add(circle);
    }

    public void setSourceEventCircles(Simulation simulation) {
        for (int i = 0; i < simulation.getMap().getEventSources().size(); i++) {
            Circle circle = circleCreator(simulation.getMap().getEventSources().get(i));
            Circle rangeCircle = rangeCreator(simulation.getMap().getEventSources().get(i));
            root.getChildren().add(circle);
            root.getChildren().add(rangeCircle);
        }
    }

    public void setStationaryPointCircles(Simulation simulation) {
        for (int i = 0; i < simulation.getMap().getStationaryNetworkPoints().size(); i++) {
            Circle circle = circleCreator(simulation.getMap().getStationaryNetworkPoints().get(i));
            simulation.getStationaryCirclelist().add(circle);
            root.getChildren().add(circle);
        }
    }

    public void setLabels(Simulation simulation, int amount) {
        for (int i = 0; i < simulation.getMap().getStationaryNetworkPoints().size(); i++) {
            Label label = labelCreator(simulation.getMap().getStationaryNetworkPoints().get(i));
            root.getChildren().add(label);
        }

        for (int i = simulation.getMap().getVehicles().size() - amount; i < simulation.getMap()
                .getVehicles()
                .size(); i++) {
            Label label = labelCreator(simulation.getMap().getVehicles().get(i));
            simulation.getLabelList().add(label);
            root.getChildren().add(label);
        }
    }
}
    