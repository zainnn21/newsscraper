package com.example.newsscraper.repository;

import com.example.newsscraper.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface repositori untuk operasi CRUD pada entitas Article.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByUrl(String url);
}
