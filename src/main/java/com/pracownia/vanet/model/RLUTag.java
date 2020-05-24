package com.pracownia.vanet.model;

import java.util.Objects;

public class RLUTag {
    final int rluId;
    final int timestamp;

    public RLUTag(int rluId, int timestamp) {
        this.rluId = rluId;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RLUTag)) return false;
        RLUTag rluTag = (RLUTag) o;
        return rluId == rluTag.rluId &&
                timestamp == rluTag.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rluId, timestamp);
    }

    @Override
    public String toString() {
        return "{" + rluId + "," + timestamp + "}->";
    }
}
