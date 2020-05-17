package com.pracownia.vanet.model;

import com.pracownia.vanet.model.point.Point;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Getter
@Setter
public class Route {

    /*------------------------ FIELDS REGION ------------------------*/
    private Point startPoint;
    private Point endPoint;
    private double speedLimit;
    private int numOfTLTE; //number of traffic lanes to route end
    private int numOfTLTS; //number of traffic lanes to route start

    /*------------------------ METHODS REGION ------------------------*/
    public Route(double xStartPoint, double yStartPoint, double xEndPoint, double yEndPoint, double speedLimit, int numOfTLTE, int numOfTLTS) {
        this.startPoint = new Point(xStartPoint, yStartPoint);
        this.endPoint = new Point(xEndPoint, yEndPoint);
        this.speedLimit = speedLimit;
        this.numOfTLTE = numOfTLTE;
        this.numOfTLTS = numOfTLTS;
    }

    public double getDistance() {
        return Math.sqrt(Math.pow(endPoint.getX() - startPoint.getX(), 2)
                + Math.pow(endPoint.getY() - startPoint.getY(), 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Route route = (Route) o;

        return new EqualsBuilder()
                .append(startPoint, route.startPoint)
                .append(endPoint, route.endPoint)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(startPoint)
                .append(endPoint)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("startPoint", startPoint)
                .append("endPoint", endPoint)
                .toString();
    }
}
    