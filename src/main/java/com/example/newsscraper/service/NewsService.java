package com.example.newsscraper.service;

import com.example.newsscraper.model.Article;
import com.example.newsscraper.repository.ArticleRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> fetchArticlesFromRss() throws IOException{
        List<Article> articles = new ArrayList<>();
        // Mengambil dokumen RSS dari URL
        Document document = Jsoup.connect("https://jambi.antaranews.com/rss/terkini.xml").get();
        // Mengiterasi setiap item di RSS feed
        for (Element item : document.select("item")){
            String url = item.select("link").text();
            String title = item.select("title").text();
            String pubDate = item.select("pubDate").text();
            Long publishTime = Instant.parse(pubDate).getEpochSecond();

            Article article = new Article();
            article.setUrl(url);
            article.setTitle(title);
            article.setArticleTs(publishTime);
            article.setContent(fethContentUrl(url));

            // Menambahkan artikel ke daftar dan menyimpannya di database
            articles.add(article);
            articleRepository.save(article);
        }
        return articles;
    }

    private String fethContentUrl(String url) throws IOException{
        // Mengambil konten artikel dari URL menggunakan Jsoup
        Document document = Jsoup.connect(url).get();
        return document.select(".article-content").text();
    }
}
