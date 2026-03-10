package com.example.Refactoring.model;

/*
 @author kings
 @project Refactoring
 @class Reminder
 @version 1.0.0
 @since 10.03.2026 - 3:16
*/
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;



public class Reminder {

    private String date;
    private String time;
    private String message;

    public Reminder(String date, String time, String message) {
        this.date = date;
        this.time = time;
        this.message = message;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getMessage() { return message; }

    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setMessage(String message) { this.message = message; }
}
