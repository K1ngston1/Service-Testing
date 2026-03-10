package com.example.Refactoring.repository;

/*
 @author kings
 @project Refactoring
 @class UserRepository
 @version 1.0.0
 @since 10.03.2026 - 2:57
*/


import com.example.Refactoring.model.UserProfile;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserProfile, Long> {
}