package com.pracownia.vanet.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Direction {
    public DirectionEnum direction;

    public DirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(DirectionEnum direction) {
        this.direction = direction;
    }
    public DirectionEnum getOpposite() {
        if (this.direction == DirectionEnum.UP) return DirectionEnum.DOWN;
        if (this.direction == DirectionEnum.DOWN) return DirectionEnum.UP;
        if (this.direction == DirectionEnum.LEFT) return DirectionEnum.RIGHT;
        return DirectionEnum.LEFT;
    }
}
