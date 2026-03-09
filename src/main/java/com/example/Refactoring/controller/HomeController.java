package com.example.Refactoring.controller;

/*
 @author kings
 @project Refactoring
 @class HomeController
 @version 1.0.0
 @since 04.03.2026 - 19:32
*/
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "weather", "/weather",
                "currency", "/currency",
                "news", "/news",
                "message", "API is running"
        );
    }
}
