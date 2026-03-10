package com.example.Refactoring.service;

/*
 @author kings
 @project Refactoring
 @class UserService
 @version 1.0.0
 @since 10.03.2026 - 2:57
*/


import com.example.Refactoring.controller.UserProfile;
import com.example.Refactoring.repository.UserRepository;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

    public UserProfile getOrCreateUser(Long chatId,String name){

        return repository.findById(chatId)
                .orElseGet(() ->
                        repository.save(new UserProfile(chatId,name)));
    }

    public void register(Long chatId){

        UserProfile user = repository.findById(chatId).orElseThrow();

        user.setRegistered(true);

        repository.save(user);
    }

    public void setCity(Long chatId,String city){

        UserProfile user = repository.findById(chatId).orElseThrow();

        user.setFavoriteCity(city);

        repository.save(user);
    }

    public void setLanguage(Long chatId,String lang){

        UserProfile user = repository.findById(chatId).orElseThrow();

        user.setLanguage(lang);

        repository.save(user);
    }

    public void setTimeFormat(Long chatId,String format){

        UserProfile user = repository.findById(chatId).orElseThrow();

        user.setTimeFormat(format);

        repository.save(user);
    }

}