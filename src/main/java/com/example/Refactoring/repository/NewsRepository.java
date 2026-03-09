package com.example.Refactoring.repository;
import com.example.Refactoring.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 @author kings
 @project Refactoring
 @class NewsRepository
 @version 1.0.0
 @since 04.03.2026 - 19:17
*/

@Repository
public interface NewsRepository extends MongoRepository<News, String> {

    List<News> findByAuthor(String author);

}