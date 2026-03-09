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

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_KEY = "ebb54148153056fc5195d0e56cd1c9bb";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "weatherCache", key = "#city")
    public Weather getWeatherByCity(String city) {

        logger.info("Requesting weather for city: {}", city);

        String url = UriComponentsBuilder
                .fromHttpUrl("https://api.openweathermap.org/data/2.5/weather")
                .queryParam("q", city)
                .queryParam("appid", API_KEY)
                .queryParam("units", "metric")
                .toUriString();

        try {

            String response = restTemplate.getForObject(url, String.class);

            logger.info("Response received from OpenWeather");

            JsonNode root = objectMapper.readTree(response);

            double temp = root.path("main").path("temp").asDouble();
            String description = root.path("weather").get(0).path("description").asText();

            return new Weather(city, temp, description);

        } catch (Exception e) {

            logger.error("Error while requesting weather API: {}", e.getMessage());

            return new Weather(city, 0.0, "API Error");
        }
    }
}