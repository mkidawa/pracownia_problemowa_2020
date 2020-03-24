package com.pracownia.vanet.old;

import lombok.Data;

import java.util.Date;

@Data
public class Event {
    private int id;
    private EventType eventType;
    private Date eventDate;
    private String message;
}
