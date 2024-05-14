package com.example.newsscraper.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class XPathUtil {

    public static String fetchContentFromUrl(String url) throws IOException{
        // Mengambil konten artikel dari URL menggunakan Jsoup dan XPath
        Document document = Jsoup.connect(url).get();
        Elements content = document.select(".article-content");
        return content.text();
    }
}
