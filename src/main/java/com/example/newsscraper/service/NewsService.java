package com.example.newsscraper.service;


import com.example.newsscraper.model.Article;
import com.example.newsscraper.repository.ArticleRepository;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParagraph;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

/**
 * Kelas service untuk mengambil artikel berita dari RSS Feed dan halaman index.
 */
@Service
public class NewsService {

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Mengambil artikel dari RSS Feed.
     *
     * @return Daftar artikel yang diambil dari RSS Feed
     * @throws IOException jika terjadi kesalahan I/O saat pengambilan data
     */
    @Transactional
    public List<Article> fetchArticlesFromRss() throws IOException {
        // Membuat daftar kosong untuk menyimpan artikel yang diambil dari RSS feed
        List<Article> articles = new ArrayList<>();

        // Mengambil dokumen HTML dari URL RSS feed menggunakan Jsoup
        Document doc = Jsoup.connect("https://jambi.antaranews.com/rss/terkini.xml").get();

        // Iterasi melalui setiap elemen <item> dalam dokumen RSS untuk mendapatkan informasi artikel
        for (Element item : doc.select("item")) {
            // Mengambil URL, judul, dan konten artikel dari elemen <item>
            String url = item.select("link").text();
            if(articleRepository.existsByUrl(url)){
                System.out.println("URL already exists, skipping: " + url);
                continue;
            }
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
            System.out.println("Success Insert Data: "+ article);
        }

        // Mengembalikan daftar artikel yang telah diambil dan disimpan
        return articles;
    }


    /**
     * Mengambil artikel dari halaman indeks.
     *
     * @return Daftar artikel yang diambil dari halaman indeks
     * @throws IOException jika terjadi kesalahan I/O saat pengambilan data
     */
    @Transactional
    public List<Article> fetchArticlesFromIndex() throws IOException {
        List<String> urls = new ArrayList<>();
        List<Article> articles = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        // Kumpulkan URL dari halaman utama
        Document document = Jsoup.connect("https://www.bisnis.com/index").get();
        for (Element item : document.select(".list-news.indeks-new li")) {
            String url = item.select(".col-sm-8 a").attr("href");
            String title = item.select(".col-sm-8 a").text();
            urls.add(url);
            titles.add(title);
        }

        // Iterasi melalui daftar URL dan ambil konten untuk setiap URL
        for (int i = 0; i < urls.size(); i++) {
            String url = urls.get(i);
            String title = titles.get(i);
            if (articleRepository.existsByUrl(url)){
                // Jika artikel dengan URL yang sama sudah ada, lewati proses pengambilan artikel baru
                System.out.println("URL already exists, skipping: " + url);
                continue; // Skip proses insert dan lanjut ke URL berikutnya
            }

            // Buat objek Article untuk artikel baru
            Article article = new Article();
            article.setUrl(url);
            article.setTitle(title);
            article.setArticleTs(Instant.now().getEpochSecond());
            article.setContent(fetchContentFromUrlWithXPath(url));
            articles.add(article);
            articleRepository.save(article);
            System.out.println("Success Insert Data: "+ article);
        }
        // Mengembalikan daftar artikel yang telah diambil dan disimpan
        return articles;
    }

    /**
     * Mengambil konten artikel dari URL menggunakan XPath.
     *
     * @param url URL artikel
     * @return Konten artikel
     * @throws IOException jika terjadi kesalahan I/O saat pengambilan data
     */
    private String fetchContentFromUrlWithXPath(String url) throws IOException {
        try (WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER)) {
            // Konfigurasi WebClient untuk menonaktifkan JavaScript dan CSS
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setCssEnabled(false);
            // Mengambil halaman HTML menggunakan HtmlUnit
            HtmlPage page = webClient.getPage(url);
            // Menggunakan XPath untuk menemukan elemen konten artikel
            List<HtmlParagraph> paragraphs = page.getByXPath("//article[@class='detailsContent force-17 mt40']/p");
            // Membangun konten artikel dari teks setiap elemen yang ditemukan
            StringBuilder contentBuilder = new StringBuilder();
            for (HtmlParagraph paragraph : paragraphs) {
                contentBuilder.append(paragraph.asText()).append("\n"); // Mengambil teks dari setiap elemen yang ditemukan
            }
            // Mengembalikan konten artikel dalam bentuk string
            return contentBuilder.toString();
        }
    }
}


