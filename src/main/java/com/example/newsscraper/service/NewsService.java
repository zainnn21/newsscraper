package com.example.newsscraper.service;


import com.example.newsscraper.model.Article;
import com.example.newsscraper.repository.ArticleRepository;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
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
        Document document = Jsoup.connect("https://www.bisnis.com/index").get();
        for (Element item : document.select(".col-custom.left")) {
            String url = item.select(".col-sm-8 a").attr("href");
            String title = item.select(".col-sm-8 a").attr("title");
//            String publishedDt = item.select("h2").attr("small");
//            String publishedDate = publishedDt.substring(publishedDt.indexOf(":") +2).trim();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");
//            LocalDateTime pubDate = LocalDateTime.parse(publishedDate, formatter);
//            LocalDate pubDt = pubDate.toLocalDate();

            long articleTimestamp = Instant.now().getEpochSecond();

            if (articleRepository.existsByUrl(url)){
                System.out.println("URL already exists, skipping: " + url);
                continue; // Skip proses insert dan lanjut ke looping berikutnya
            }
            Article article = new Article();
            article.setUrl(url);
            article.setTitle(title);
            article.setArticleTs(articleTimestamp);
//            article.setPublishedDate(pubDt);
            article.setContent(fetchContentFromUrlWithXPath(url));
            articles.add(article);
            articleRepository.save(article);
        }
        return articles;
    }

    private String fetchContentFromUrlWithXPath(String url) throws IOException {
        try (WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER)) {
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            HtmlPage page = webClient.getPage(url); // Mengambil halaman HTML menggunakan HtmlUnit
            List<HtmlParagraph> paragraphs = page.getByXPath("//article[@class='detailsContent force-17 mt40']/p"); // Menggunakan XPath untuk menemukan elemen

            StringBuilder contentBuilder = new StringBuilder();
            for (HtmlParagraph paragraph : paragraphs) {
                contentBuilder.append(paragraph.asText()).append("\n"); // Mengambil teks dari setiap elemen yang ditemukan
            }
            return contentBuilder.toString(); // Mengembalikan konten dalam bentuk string
        }
    }
}


