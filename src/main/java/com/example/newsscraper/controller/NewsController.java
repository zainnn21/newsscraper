package com.example.newsscraper.controller;

import com.example.newsscraper.model.Article;
import com.example.newsscraper.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Kelas kontroler untuk menangani endpoint terkait news.
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    /**
     * Endpoint untuk mengambil artikel dari RSS Feed.
     *
     * @return Daftar artikel yang diambil dari umpan RSS
     * @throws IOException jika terjadi kesalahan I/O saat pengambilan data
     */
    @GetMapping("/fetch-rss")
    public List<Article> fetchArticlesFromRss() throws IOException {
        return newsService.fetchArticlesFromRss();
    }

    /**
     * Endpoint untuk mengambil artikel dari halaman indeks.
     *
     * @return Daftar artikel yang diambil dari halaman indeks
     * @throws IOException jika terjadi kesalahan I/O saat pengambilan data
     */
    @GetMapping("/fetch-index")
    public List<Article> fetchArticlesFromIndex() throws IOException {
        return newsService.fetchArticlesFromIndex();
    }
}
