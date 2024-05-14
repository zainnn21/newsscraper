package com.example.newsscraper.controller;

import com.example.newsscraper.model.Article;
import com.example.newsscraper.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping("/fetch-rss")
    public List<Article> fetchArticlesFromRss() throws IOException {
        return newsService.fetchArticlesFromRss();
    }

    @GetMapping("/fetch-index")
    public List<Article> fetchArticlesFromIndex() throws IOException {
        return newsService.fetchArticlesFromIndex();
    }
}
