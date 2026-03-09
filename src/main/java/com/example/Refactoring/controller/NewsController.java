package com.example.Refactoring.controller;
import com.example.Refactoring.model.News;
import com.example.Refactoring.repository.NewsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping(
"/news")
public class NewsController {

    private final NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    // GET all news
    @GetMapping
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    // GET by author
    @GetMapping("/author/{author}")
    public List<News> getByAuthor(@PathVariable String author) {
        return newsRepository.findByAuthor(author);
    }

    // POST new news
    @PostMapping
    public News addNews(@RequestBody News news) {
        return newsRepository.save(news);
    }

    // DELETE by id
    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsRepository.deleteById(id);
    }
}