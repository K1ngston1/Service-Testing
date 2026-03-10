package com.example.Refactoring.model;

import com.example.Refactoring.model.Reminder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Document(collection = "users")
public class UserProfile {

    @Id
    private Long chatId;
    private String name;
    private String language;
    private String favoriteCity;
    private String timeFormat;
    private boolean registered;
    private List<Reminder> reminders = new ArrayList<>();

    public UserProfile() {}

    public UserProfile(Long chatId, String name){
        this.chatId = chatId;
        this.name = name;
        this.language = "en";
        this.favoriteCity = "Kyiv";
        this.timeFormat = "24h";
        this.registered = false;
    }

    public Long getChatId() { return chatId; }
    public String getName() { return name; }
    public String getLanguage() { return language; }
    public String getFavoriteCity() { return favoriteCity; }
    public String getTimeFormat() { return timeFormat; }
    public boolean isRegistered() { return registered; }
    public List<Reminder> getReminders() { return reminders; }

    public void setName(String name) { this.name = name; }
    public void setLanguage(String language) { this.language = language; }
    public void setFavoriteCity(String favoriteCity) { this.favoriteCity = favoriteCity; }
    public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }
    public void setRegistered(boolean registered) { this.registered = registered; }
    public void addReminder(Reminder reminder) { this.reminders.add(reminder); }
}