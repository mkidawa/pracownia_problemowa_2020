package com.pracownia.vanet;

import com.pracownia.vanet.model.Vehicle;
import com.pracownia.vanet.util.Logger;
import com.pracownia.vanet.view.ShapesCreator;
import com.pracownia.vanet.view.Simulation;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Main extends Application {

    /*------------------------ FIELDS REGION ------------------------*/
    private CheckBox seeThrough;
    private TextField trustLevelField;
    private TextField speedField;
    private TextField vehIdField;
    private TextField connEventsField;
    private TextField connVehField;
    private TextField connPointsField;
    private Group root = new Group();
    private ShapesCreator shapesCreator;
    private boolean isRangeRendered = false;
    private Simulation simulation;
    private long startTime;

    /*------------------------ METHODS REGION ------------------------*/
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

        this.simulation = new Simulation();
        this.shapesCreator = new ShapesCreator(root, this.simulation, this);

        shapesCreator.setRoutesLines(simulation);
        shapesCreator.setSourceEventCircles(simulation);
        shapesCreator.setStationaryPointCircles(simulation);

        setInterface(simulation);

        Scene scene = new Scene(root, 1400, 850);

        stage.setTitle("Vanet");
        stage.setScene(scene);
        stage.show();
        simulation.getTr().start();

        long startTime = System.currentTimeMillis();
        double durationTime = ((System.currentTimeMillis() - startTime) / 1000.0);
        System.out.println(durationTime + "s");

    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        System.out.println("Simulation started");
    }

    private void stopTimer() {
        double durationTime = ((System.currentTimeMillis() - startTime) / 1000.0);
        System.out.println("Duration: " + durationTime + "s");
        System.out.println("Simulation stopped");
    }

    private void setInterface(Simulation simulation) {
        Button showRangeButton = new Button("Show Range");
        Button changeRangeButton = new Button("ChangeRange");
        Button spawnVehiclesButton = new Button("Spawn Vehicles");
        Button spawnFakedVeehicle = new Button("Spawn fake vehicle");
        TextField spawnFakedVeehicleTextField = new TextField();
        TextField vehiclesAmountField = new TextField();
        TextField rangeAmountField = new TextField();
        Label rangeAmountLabel = new Label("Range");
        Label vehiclesAmountLabel = new Label("Vehicle Amount");
        ChoiceBox chooseFakeEvent = new ChoiceBox(FXCollections.observableArrayList(
                "Car accident", "Speed camera", "Police control"));

        // Start stop simulation.
        Button startSimulation = new Button("Start simulation");
        startSimulation.setLayoutX(950.0);
        startSimulation.setLayoutY(310.);
        startSimulation.setOnAction(e -> {
            simulation.setSimulationRunning(true);
            startTimer();
        });

        Button stopSimulation = new Button("Stop simulation");
        stopSimulation.setLayoutX(950.0);
        stopSimulation.setLayoutY(340.);
        stopSimulation.setOnAction(e -> {
            simulation.setSimulationRunning(false);
            simulation.logCrossingHackerCount();
            stopTimer();
        });

        Button addHackerVehicle = new Button("Add hacker vehicle");
        addHackerVehicle.setLayoutX(1130.0);
        addHackerVehicle.setLayoutY(200.00);
        addHackerVehicle.setOnAction(e -> {
            try {
            shapesCreator.setCopyCircle(simulation.getMap().addCopy());
            } catch (IllegalArgumentException exception) {
                Logger.log("Nothing to copy");
                System.out.println("Nothing to copy");
            }
        });

        Button teleportVehicle = new Button("Teleport a vehicle");
        teleportVehicle.setLayoutX(1130.0);
        teleportVehicle.setLayoutY(230.0);
        teleportVehicle.setOnAction(e -> {
            simulation.teleportVehicle();
        });

        Button saveVehicleButton = new Button("Save vehicle");
        saveVehicleButton.setLayoutX(950.0);
        saveVehicleButton.setLayoutY(280.);
        saveVehicleButton.setOnAction(e -> {
            Vehicle v = simulation.getMap()
                    .getVehicles()
                    .get(Integer.parseInt(this.vehIdField.getText()));
            v.setSpeed(Double.parseDouble(this.speedField.getText()));
            v.setTrustLevel(Double.parseDouble(this.trustLevelField.getText()));
        });

        Button clearNotSafe = new Button("Clear hackers");
        clearNotSafe.setLayoutX(1130.0);
        clearNotSafe.setLayoutY(265.0);
        clearNotSafe.setOnAction(e -> {
            simulation.deleteUnsafeCircles();
        });

        TextField amountOfDevices = new TextField();
        amountOfDevices.setLayoutX(1130.0);
        amountOfDevices.setLayoutY(520.0);
        amountOfDevices.setText("10");

        Button addSybilAttackerButton = new Button("Add sybil attacker");
        addSybilAttackerButton.setLayoutX(1130.0);
        addSybilAttackerButton.setLayoutY(460.0);


        Label amountOfSybilAttackersLabel = new Label("Amount of devices to fake");
        amountOfSybilAttackersLabel.setLayoutX(1130.0);
        amountOfSybilAttackersLabel.setLayoutY(500.0);

        // Vehicle informations.
        this.trustLevelField = new TextField();
        trustLevelField.setLayoutX(950.0);
        trustLevelField.setLayoutY(400.0);

        Label trustLevelLabel = new Label("Trust level");
        trustLevelLabel.setLayoutX(950.0);
        trustLevelLabel.setLayoutY(430.0);

        this.speedField = new TextField();
        speedField.setLayoutX(950.0);
        speedField.setLayoutY(460.0);

        Label speedLabel = new Label("Speed");
        speedLabel.setLayoutX(950.0);
        speedLabel.setLayoutY(490.0);

        this.vehIdField = new TextField();
        vehIdField.setLayoutX(950.0);
        vehIdField.setLayoutY(520.0);

        Label vehIdLabel = new Label("Veh id");
        vehIdLabel.setLayoutX(950.0);
        vehIdLabel.setLayoutY(550.0);

        this.connPointsField = new TextField();
        connPointsField.setLayoutX(950.0);
        connPointsField.setLayoutY(580.0);

        Label connPointsLabel = new Label("Conn points");
        connPointsLabel.setLayoutX(950.0);
        connPointsLabel.setLayoutY(610.0);

        this.connEventsField = new TextField();
        connEventsField.setLayoutX(950.0);
        connEventsField.setLayoutY(640.0);

        Label connEventsLabel = new Label("collectedEvents");
        connEventsLabel.setLayoutX(950.0);
        connEventsLabel.setLayoutY(670.0);

        this.connVehField = new TextField();
        connVehField.setLayoutX(950.0);
        connVehField.setLayoutY(700.0);

        Label connVehLabel = new Label("connectedVehicles");
        connVehLabel.setLayoutX(950.0);
        connVehLabel.setLayoutY(730.0);

        ListView<Vehicle> hackerVehiclesList = new ListView<>();
        hackerVehiclesList.setLayoutX(1125.0);
        hackerVehiclesList.setLayoutY(350.0);
        hackerVehiclesList.setMaxHeight(100);
        hackerVehiclesList.setMaxWidth(175.0);
        hackerVehiclesList.setItems(simulation.getMap().getVehicles());

        seeThrough = new CheckBox("Widac?");
        seeThrough.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue,
                                Boolean aBoolean, Boolean t1) {
                if (t1) {
                    simulation.setHere(Color.TRANSPARENT);
                } else {
                    simulation.setHere(Color.RED);
                }
            }
        });
        seeThrough.setLayoutX(1150);
        seeThrough.setLayoutY(155.0);

        // Other stuff.
        chooseFakeEvent.setLayoutX(1130.0);
        chooseFakeEvent.setLayoutY(80.0);
        chooseFakeEvent.setValue("Car accident");

        spawnFakedVeehicle.setLayoutX(1130.0);
        spawnFakedVeehicle.setLayoutY(110.0);
        spawnFakedVeehicleTextField.setLayoutX(1130);
        spawnFakedVeehicleTextField.setLayoutY(50);

        showRangeButton.setLayoutX(950.0);
        showRangeButton.setLayoutY(80.0);

        changeRangeButton.setLayoutX(950.0);
        changeRangeButton.setLayoutY(110.0);
        rangeAmountLabel.setLayoutX(950.0);
        rangeAmountLabel.setLayoutY(140.0);
        rangeAmountField.setLayoutX(950.0);
        rangeAmountField.setLayoutY(160.0);
        rangeAmountField.setText("40.0");

        spawnVehiclesButton.setLayoutX(950.0);
        spawnVehiclesButton.setLayoutY(190.0);
        vehiclesAmountLabel.setLayoutX(950.0);
        vehiclesAmountLabel.setLayoutY(220.0);
        vehiclesAmountField.setLayoutX(950.0);
        vehiclesAmountField.setLayoutY(240.0);
        vehiclesAmountField.setText("10");

        shapesCreator.legendCreator(100,750, Color.BLACK, "Vehicle - wrong traffic lane");
        shapesCreator.legendCreator(100,775, Color.AQUA, "Vehicle - traffic lane 1");
        shapesCreator.legendCreator(100,800, Color.GOLD, "Vehicle - traffic lane 2");
        shapesCreator.legendCreator(100,825, Color.CORAL, "Vehicle - traffic lane 3");
        shapesCreator.legendCreator(300,750, Color.DARKRED, "Vehicle - too fast");
        shapesCreator.legendCreator(300,775, Color.BLUE, "Stationary network point");
        shapesCreator.legendCreator(300,800, Color.RED, "Route event");


        changeRangeButton.setOnAction(e -> simulation.changeVehiclesRanges(Double.parseDouble(rangeAmountField
                .getText())));

        showRangeButton.setOnAction(e -> {
            isRangeRendered = !isRangeRendered;
            if (isRangeRendered) {
                simulation.switchOnRangeCircles();
            } else {
                simulation.switchOffRangeCircles();
            }
        });

        addSybilAttackerButton.setOnAction(e -> {
            simulation.getMap().addSybilAttacker(Integer.valueOf(amountOfDevices.getText()));
            shapesCreator.setVehicleCircles(simulation, 1);
            shapesCreator.setLabels(simulation, 1);
        });

        spawnFakedVeehicle.setOnAction(e -> {
            Integer numberOfFakeVehicle = Integer.valueOf(spawnFakedVeehicleTextField.getText());
            for (int i = 0; i < numberOfFakeVehicle; i++) {
                simulation.getMap().addFakeVehicle(chooseFakeEvent.getValue().toString());
                shapesCreator.setVehicleCircles(simulation, 1);
                shapesCreator.setLabels(simulation, 1);
            }
        });

        spawnVehiclesButton.setOnAction(e -> {
            simulation.getMap().addVehicles(Integer.parseInt(vehiclesAmountField.getText()));
            shapesCreator.setVehicleCircles(simulation,
                    Integer.parseInt(vehiclesAmountField.getText()));
            shapesCreator.setLabels(simulation, Integer.parseInt(vehiclesAmountField.getText()));
        });

        root.getChildren()
                .addAll(chooseFakeEvent,
                        spawnFakedVeehicle,
                        spawnFakedVeehicleTextField,
                        showRangeButton,
                        spawnVehiclesButton,
                        vehiclesAmountField,
                        stopSimulation,
                        saveVehicleButton,
                        trustLevelField,
                        trustLevelLabel,
                        speedField,
                        speedLabel,
                        vehIdField,
                        vehIdLabel,
                        connPointsField,
                        connPointsLabel,
                        connEventsField,
                        connEventsLabel,
                        connVehField,
                        connVehLabel,
                        startSimulation,
                        vehiclesAmountLabel,
                        rangeAmountLabel,
                        rangeAmountField,
                        changeRangeButton,
                        teleportVehicle,
                        addHackerVehicle,
                        clearNotSafe,
                        hackerVehiclesList,
                        addSybilAttackerButton,
                        amountOfDevices,
                        amountOfSybilAttackersLabel,
                        seeThrough);
    }
}
    