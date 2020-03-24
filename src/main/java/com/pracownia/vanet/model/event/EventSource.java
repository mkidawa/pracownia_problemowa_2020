package com.pracownia.vanet.model.event;

import com.pracownia.vanet.model.point.Point;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class EventSource {

    /*------------------------ FIELDS REGION ------------------------*/
    private int id;
    private String name;
    private String description;
    private Point localization;
    private Date eventDate;
    private Double range;
    private EventType eventType;

    /*------------------------ METHODS REGION ------------------------*/
    public EventSource(int id, String name, String description,
                       Point localization, Date eventDate, Double range,
                       EventType eventType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.localization = localization;
        this.eventDate = eventDate;
        this.range = range;
        this.eventType = eventType;
    }

    public Event getEvent() {
        Event event = new Event();
        event.setId(id);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setMessage("Event id: " + event.getId() + "\r\n" +
                "Localisation: " + localization + "\r\n" +
                "Event type: " + eventType + "\r\n" +
                "Event Date: " + eventDate + "\r\n");

        return event;
    }

    public boolean isInRange(Point vehicleLocalisation) {
        Double distance = Math.sqrt(Math.pow(localization.getX() - vehicleLocalisation.getX(), 2) +
                Math.pow(localization.getY() - vehicleLocalisation.getY(), 2));

        if (distance < range) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventSource that = (EventSource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(description, that.description)
                .append(localization, that.localization)
                .append(eventDate, that.eventDate)
                .append(range, that.range)
                .append(eventType, that.eventType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(description)
                .append(localization)
                .append(eventDate)
                .append(range)
                .append(eventType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("description", description)
                .append("localization", localization)
                .append("eventDate", eventDate)
                .append("range", range)
                .append("eventType", eventType)
                .toString();
    }
}
    