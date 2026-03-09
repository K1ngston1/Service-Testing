package com.example.Refactoring.controller;
import com.example.Refactoring.model.Currency;
import com.example.Refactoring.repository.CurrencyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/*
 @author kings
 @project Refactoring
 @class CurrencyController
 @version 1.0.0
 @since 04.03.2026 - 19:16
*/
@RestController
@RequestMapping("/currency")
public class CurrencyController {

    private final CurrencyRepository currencyRepository;

    public CurrencyController(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @GetMapping
    public List<Currency> getAllCurrency() {
        return currencyRepository.findAll();
    }

    @GetMapping("/{name}")
    public Currency getByName(@PathVariable String name) {
        Optional<Currency> currency = currencyRepository.findByName(name);
        return currency.orElseThrow(() -> new RuntimeException("Currency not found"));
    }

    @PostMapping
    public Currency addCurrency(@RequestBody Currency currency) {
        return currencyRepository.save(currency);
    }

    @DeleteMapping("/{id}")
    public void deleteCurrency(@PathVariable String id) {
        currencyRepository.deleteById(id);
    }
}