package com.example.newsscraper.service;


import com.example.newsscraper.model.Article;
import com.example.newsscraper.repository.ArticleRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
public class NewsService {

    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> fetchArticlesFromRss() throws IOException {
        // Membuat daftar kosong untuk menyimpan artikel yang diambil dari RSS feed
        List<Article> articles = new ArrayList<>();

        // Mengambil dokumen HTML dari URL RSS feed menggunakan Jsoup
        Document doc = Jsoup.connect("https://jambi.antaranews.com/rss/terkini.xml").get();

        // Iterasi melalui setiap elemen <item> dalam dokumen RSS untuk mendapatkan informasi artikel
        for (Element item : doc.select("item")) {
            // Mengambil URL, judul, dan konten artikel dari elemen <item>
            String url = item.select("link").text();
            String title = item.select("title").text();

            // Mengambil tanggal publikasi artikel dari elemen <pubDate> dan mem-parsingnya
            String pubDateStr = item.select("pubDate").text();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime pubDate = LocalDateTime.parse(pubDateStr, formatter);
            LocalDate publishedDate = pubDate.toLocalDate();

            // Mengambil konten artikel dari elemen <description> dan membersihkannya dari tag HTML
            String content = item.select("description").text();
            String cleanedContent = Jsoup.parse(content).text();

            // Menggunakan waktu saat ini sebagai timestamp untuk artikel
            long articleTimestamp = Instant.now().getEpochSecond();

            // Membuat objek Article dari informasi yang diambil
            Article article = new Article();
            article.setUrl(url);
            article.setTitle(title);
            article.setArticleTs(articleTimestamp);
            article.setContent(cleanedContent);
            article.setPublishedDate(publishedDate);

            // Menambahkan artikel ke dalam daftar artikel
            articles.add(article);

            // Menyimpan artikel ke dalam basis data menggunakan repository articleRepository
            articleRepository.save(article);
        }

        // Mengembalikan daftar artikel yang telah diambil dan disimpan
        return articles;
    }


    public List<Article> fetchArticlesFromIndex() throws IOException {
        List<Article> articles = new ArrayList<>();
        // Mengambil dokumen RSS dari URL
        Document document = Jsoup.connect("https://jambi.antaranews.com/rss/terkini.xml").get();
        // Mengiterasi setiap item di RSS feed
        for (Element item : document.select("item")) {
            String url = item.select("link").text();
            String title = item.select("title").text();
            String pubDate = item.select("pubDate").text();
            long articleTimestamp = Instant.now().getEpochSecond(); // Menggunakan waktu saat ini sebagai timestamp artikel

            Article article = new Article();
            article.setUrl(url);
            article.setTitle(title);
            article.setArticleTs(articleTimestamp);
            article.setPublishedDate(LocalDate.parse(pubDate));
            article.setContent(fetchContentFromUrlWithXPath(url));

            articles.add(article);
        }
        return articles;
    }

    private String fetchContentFromUrlWithXPath(String url) throws IOException {
        Document doc = Jsoup.connect(url).get(); // Mengambil halaman HTML menggunakan Jsoup
        String xpathExpression = "//article[@class='detailsContent force-17 mt40']/p"; // XPath untuk elemen yang berisi konten artikel
        Elements elements = doc.select(xpathExpression); // Menggunakan XPath untuk menemukan elemen

        StringBuilder contentBuilder = new StringBuilder();
        for (Element element : elements) {
            contentBuilder.append(element.text()).append("\n"); // Mengambil teks dari setiap elemen yang ditemukan
        }
        return contentBuilder.toString(); // Mengembalikan konten dalam bentuk string
    }
}


