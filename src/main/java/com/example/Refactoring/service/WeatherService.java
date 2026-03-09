package com.example.Refactoring.service;

import com.example.Refactoring.model.Weather;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.Refactoring.model.Weather;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "ebb54148153056fc5195d0e56cd1c9bb";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "weatherCache", key = "#city")
    public Weather getWeatherByCity(String city) {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }

        try {
            String url = String.format(
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=%s",
                    city,
                    API_KEY
            );

            Map response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.get("main") == null) {
                return new Weather(city, 0.0, "API Error");
            }

            Map<String, Object> main = (Map<String, Object>) response.get("main");
            double temp = ((Number) main.get("temp")).doubleValue();
            String description = ((Map<String, Object>) ((List) response.get("weather")).get(0))
                    .get("description").toString();

            return new Weather(city, temp, description);

        } catch (Exception e) {
            logger.error("Error while requesting weather API: {}", e.getMessage());
            return new Weather(city, 0.0, "API Error");
        }
    }
}