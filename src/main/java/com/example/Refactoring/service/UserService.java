package com.example.Refactoring.service;

/*
 @author kings
 @project Refactoring
 @class UserService
 @version 1.0.0
 @since 10.03.2026 - 2:57
*/


import com.example.Refactoring.model.Reminder;
import com.example.Refactoring.model.UserProfile;
import com.example.Refactoring.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserProfile getOrCreateUser(Long chatId, String name) {
        return repository.findById(chatId)
                .orElseGet(() -> repository.save(new UserProfile(chatId, name)));
    }

    public void register(Long chatId) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.setRegistered(true);
        repository.save(user);
    }

    public void setCity(Long chatId, String city) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.setFavoriteCity(city);
        repository.save(user);
    }

    public void setLanguage(Long chatId, String lang) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.setLanguage(lang);
        repository.save(user);
    }

    public void setTimeFormat(Long chatId, String format) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.setTimeFormat(format);
        repository.save(user);
    }

    public void addReminder(Long chatId, Reminder reminder) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.addReminder(reminder);
        repository.save(user);
    }

    public List<Reminder> getReminders(Long chatId) {
        return repository.findById(chatId)
                .map(UserProfile::getReminders)
                .orElse(List.of());
    }

    public void deleteReminder(Long chatId, int index) {
        UserProfile user = repository.findById(chatId).orElseThrow();
        user.removeReminder(index);
        repository.save(user);
    }
    public void updateReminder(Long chatId, int index, Reminder updatedReminder) {
        UserProfile user = repository.findById(chatId).orElseThrow();

        if (user.getReminders() == null || index < 0 || index >= user.getReminders().size()) {
            throw new RuntimeException("Invalid reminder index");
        }

        user.getReminders().set(index, updatedReminder);
        repository.save(user);
    }


    public void saveUser(UserProfile user) {
        repository.save(user);
    }

    public List<UserProfile> getAllUsers() {
        return repository.findAll();
    }

}