package com.example.Refactoring.controller;

/*
 @author kings
 @project Refactoring
 @class WeatherController
 @version 1.0.0
 @since 04.03.2026 - 19:15
*/

import com.example.Refactoring.model.Weather;
import com.example.Refactoring.repository.WeatherRepository;
import com.example.Refactoring.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;
    private final WeatherRepository weatherRepository;

    public WeatherController(WeatherService weatherService, WeatherRepository weatherRepository) {
        this.weatherService = weatherService;
        this.weatherRepository = weatherRepository;
    }


    // GET all
    @GetMapping
    public List<Weather> getAllWeather() {
        return weatherRepository.findAll();
    }

    // GET by city
    @GetMapping("/{city}")
    public List<Weather> getByCity(@PathVariable String city) {
        return weatherRepository.findByCity(city);
    }

    @GetMapping("/external/{city}")
    public Weather getExternalWeather(@PathVariable String city) {
        return weatherService.getWeatherByCity(city);
    }
    // POST
    @PostMapping
    public Weather addWeather(@RequestBody Weather weather) {
        return weatherRepository.save(weather);
    }

    // PUT (update)
    @PutMapping("/{id}")
    public Weather updateWeather(@PathVariable String id, @RequestBody Weather updatedWeather) {
        return weatherRepository.findById(id)
                .map(weather -> {
                    weather.setCity(updatedWeather.getCity());
                    weather.setTemperature(updatedWeather.getTemperature());
                    weather.setDescription(updatedWeather.getDescription());
                    return weatherRepository.save(weather);
                })
                .orElseThrow(() -> new RuntimeException("Weather not found"));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteWeather(@PathVariable String id) {
        weatherRepository.deleteById(id);
    }
}