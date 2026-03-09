package com.example.Refactoring.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/*
 @author kings
 @project Refactoring
 @class News
 @version 1.0.0
 @since 04.03.2026 - 19:16
*/

@Document(collection = "news")
public class News {

    @Id
    private String id;

    private String title;
    private String content;
    private String author;

    public News() {
    }

    public News(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}