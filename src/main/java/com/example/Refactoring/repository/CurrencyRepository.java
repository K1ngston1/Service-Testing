package com.example.Refactoring.repository;
import com.example.Refactoring.model.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 @author kings
 @project Refactoring
 @class CurrencyRepository
 @version 1.0.0
 @since 04.03.2026 - 19:16
*/

@Repository
public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findByName(String name);

}
