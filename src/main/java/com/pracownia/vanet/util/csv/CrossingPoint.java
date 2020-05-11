package com.pracownia.vanet.util.csv;

import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
public class CrossingPoint {

    /*------------------------ FIELDS REGION ------------------------*/
    private Double pointX;
    private Double pointY;
    private Integer numberOfHackers;

    /*------------------------ METHODS REGION ------------------------*/
    public CrossingPoint(Double pointX, Double pointY, Integer numberOfHackers) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.numberOfHackers = numberOfHackers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CrossingPoint that = (CrossingPoint) o;

        return new EqualsBuilder()
                .append(pointX, that.pointX)
                .append(pointY, that.pointY)
                .append(numberOfHackers, that.numberOfHackers)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(pointX)
                .append(pointY)
                .append(numberOfHackers)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pointX", pointX)
                .append("pointY", pointY)
                .append("numberOfHackers", numberOfHackers)
                .toString();
    }
}
    