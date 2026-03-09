package com.example.Refactoring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 @author kings
 @project Refactoring
 @class Currency
 @version 1.0.0
 @since 04.03.2026 - 19:16
*/

@Document(collection = "currency")
public class Currency {

    @Id
    private String id;
    private String name;
    private double rate;

    public Currency() {
    }

    public Currency(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}