package com.pracownia.vanet.model.event;

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
public class Event {

    /*------------------------ FIELDS REGION ------------------------*/
    private int id;
    private EventType eventType;
    private Date eventDate;
    private String message;

    /*------------------------ METHODS REGION ------------------------*/
    public Event(int id, EventType eventType, Date eventDate, String message) {
        this.id = id;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event event = (Event) o;

        return new EqualsBuilder()
                .append(id, event.id)
                .append(eventType, event.eventType)
                .append(eventDate, event.eventDate)
                .append(message, event.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(eventType)
                .append(eventDate)
                .append(message)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("eventType", eventType)
                .append("eventDate", eventDate)
                .append("message", message)
                .toString();
    }
}
    