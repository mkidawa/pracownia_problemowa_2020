package com.pracownia.vanet.util.csv;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CsvRecord {

    /*------------------------ FIELDS REGION ------------------------*/
    private Integer timeToAllDetection;
    private Integer numberOfOrdinaryVehicle;
    private Integer numberOfAttackers;
    private Double attackerToOrdinaryRatio;
    private List<CrossingPoint> crossingPoints;

    /*------------------------ METHODS REGION ------------------------*/
    public CsvRecord(Integer timeToAllDetection, Integer numberOfOrdinaryVehicle,
                     Integer numberOfAttackers, Double attackerToOrdinaryRatio,
                     List<CrossingPoint> crossingPoints) {
        this.timeToAllDetection = timeToAllDetection;
        this.numberOfOrdinaryVehicle = numberOfOrdinaryVehicle;
        this.numberOfAttackers = numberOfAttackers;
        this.attackerToOrdinaryRatio = attackerToOrdinaryRatio;
        this.crossingPoints = crossingPoints;
    }

    public String[] getWholeHeader() {
        List<String> list = new ArrayList<>();

        list.add("timeToAllDetection");
        list.add("numberOfOrdinaryVehicle");
        list.add("numberOfAttackers");
        list.add("attackerToOrdinaryRatio");

        for (int i = 0; i < crossingPoints.size(); i++) {
            list.add("X");
            list.add("Y");
            list.add("attackerNumber");
        }

        return list.stream().toArray(String[]::new);
    }

    public String[] toStringArray() {
        List<String> list = new ArrayList<>();
        list.add(String.valueOf(timeToAllDetection));
        list.add(String.valueOf(numberOfOrdinaryVehicle));
        list.add(String.valueOf(numberOfAttackers));
        list.add(String.valueOf(attackerToOrdinaryRatio));

        crossingPoints.forEach((it) -> {
            list.add(String.valueOf(it.getPointX()));
            list.add(String.valueOf(it.getPointY()));
            list.add(String.valueOf(it.getNumberOfHackers()));
        });

        return list.stream().toArray(String[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CsvRecord csvRecord = (CsvRecord) o;

        return new EqualsBuilder()
                .append(timeToAllDetection, csvRecord.timeToAllDetection)
                .append(numberOfOrdinaryVehicle, csvRecord.numberOfOrdinaryVehicle)
                .append(numberOfAttackers, csvRecord.numberOfAttackers)
                .append(attackerToOrdinaryRatio, csvRecord.attackerToOrdinaryRatio)
                .append(crossingPoints, csvRecord.crossingPoints)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(timeToAllDetection)
                .append(numberOfOrdinaryVehicle)
                .append(numberOfAttackers)
                .append(attackerToOrdinaryRatio)
                .append(crossingPoints)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("timeToAllDetection", timeToAllDetection)
                .append("numberOfOrdinaryVehicle", numberOfOrdinaryVehicle)
                .append("numberOfAttackers", numberOfAttackers)
                .append("attackerToOrdinaryRatio", attackerToOrdinaryRatio)
                .append("crossingPoints", crossingPoints)
                .toString();
    }
}
    