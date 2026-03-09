package com.example.Refactoring.service;

/*
 @author kings
 @project Refactoring
 @class CurrencyService
 @version 1.0.0
 @since 04.03.2026 - 23:32
*/
import com.example.Refactoring.model.Currency;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import com.example.Refactoring.model.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String API_URL = "https://hexarate.paikama.co/api/rates/EUR/UAH/latest";

    @Cacheable(value = "currencyCache")
    public Currency getCurrencyRate(String symbol) {
        logger.info("Requesting currency rate for {}", symbol);

        try {

            Map response = restTemplate.getForObject(API_URL, Map.class);

            if (response == null || !response.containsKey("data")) {
                logger.error("Currency API response is invalid: {}", response);
                return new Currency(symbol.toUpperCase(), 0.0);
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            double rate = ((Number) data.get("mid")).doubleValue(); // беремо mid

            logger.info("Currency rate received: {} = {}", symbol, rate);
            return new Currency(symbol.toUpperCase(), rate);

        } catch (Exception e) {
            logger.error("Currency API error: {}", e.getMessage());
            return new Currency(symbol.toUpperCase(), 0.0);
        }
    }

    public String getUahRates() {
        Currency uah = getCurrencyRate("UAH");
        if (uah.getRate() == 0.0) {
            return "⚠️ Could not fetch UAH rate at the moment";
        }
        return "💱 1 EUR = " + uah.getRate() + " UAH";
    }
}