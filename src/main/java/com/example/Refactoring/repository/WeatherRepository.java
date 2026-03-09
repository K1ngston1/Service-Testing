package com.example.Refactoring.repository;

/*
 @author kings
 @project Refactoring
 @class WeatherRepository
 @version 1.0.0
 @since 04.03.2026 - 19:14
*/


import com.example.Refactoring.model.Weather;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {

    List<Weather> findByCity(String city);

    Optional<Weather> findTopByCityOrderByIdDesc(String city);
}