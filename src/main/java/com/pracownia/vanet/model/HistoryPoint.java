package com.pracownia.vanet.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryPoint {
    private double x;
    private double y;
    private double speed;

    public HistoryPoint(double x, double y, double speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }
}
