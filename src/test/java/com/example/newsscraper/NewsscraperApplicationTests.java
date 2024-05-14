package com.example.newsscraper;

import com.example.newsscraper.model.Article;
import com.example.newsscraper.repository.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Kelas pengujian untuk menguji fungsionalitas terkait berita.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NewsscraperApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ArticleRepository articleRepository;


	/**
	 * Kasus uji untuk mengambil endpoint artikel.
	 */
	@Test
	void testFetchArticlesEndpoint() {
		// Persiapkan data uji
		Article article = new Article();
		article.setUrl("http://example.com/article1");
		article.setTitle("Article 1");
		article.setArticleTs(Instant.now().getEpochSecond());
		article.setContent("Konten artikel 1");
		articleRepository.save(article);

		// Kirim permintaan GET ke endpoint fetchArticles
		ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/news/fetch-rss", String.class);

		// Verifikasi bahwa permintaan berhasil (kode status 200)
		assertEquals(HttpStatus.OK, response.getStatusCode());

		// Verifikasi bahwa respons tidak kosong
		assertEquals(false, response.getBody().isEmpty());
	}
}