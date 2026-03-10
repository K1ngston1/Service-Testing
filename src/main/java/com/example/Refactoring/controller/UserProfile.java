package com.example.Refactoring.controller;

/*
 @author kings
 @project Refactoring
 @class UserProfile
 @version 1.0.0
 @since 10.03.2026 - 2:56
*/

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserProfile {

    @Id
    private Long chatId;

    private String name;
    private String language;
    private String favoriteCity;
    private String timeFormat;
    private boolean registered;

    public UserProfile(){}

    public UserProfile(Long chatId,String name){
        this.chatId = chatId;
        this.name = name;
        this.language = "en";
        this.favoriteCity = "Kyiv";
        this.timeFormat = "24h";
        this.registered = false;
    }

    public Long getChatId(){ return chatId; }

    public String getName(){ return name; }

    public String getLanguage(){ return language; }

    public String getFavoriteCity(){ return favoriteCity; }

    public String getTimeFormat(){ return timeFormat; }

    public boolean isRegistered(){ return registered; }

    public void setName(String name){ this.name = name; }

    public void setLanguage(String language){ this.language = language; }

    public void setFavoriteCity(String favoriteCity){ this.favoriteCity = favoriteCity; }

    public void setTimeFormat(String timeFormat){ this.timeFormat = timeFormat; }

    public void setRegistered(boolean registered){ this.registered = registered; }

}