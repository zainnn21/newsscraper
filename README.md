# NewsScraper

NewsScraper is an application used to fetch and store news articles from various sources such as RSS feeds and website index pages.

## Features

- Fetch news articles from RSS feeds.
- Fetch news articles from website index pages.
- Store news articles in a database.

## Installation

### System Requirements:

- Java JDK (version 8 or higher)
- MySQL
- Maven
- Docker
- Docker Compose

### Clone the Repository:
Run the following command to clone the repository:
`
https://github.com/zainnn21/newsscraper.git
`

### Configure the Database:

1. Create a database in MySQL to store news articles.
You can use the following SQL script to create the necessary tables:
`CREATE TABLE prog_test.news_article (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT '',
  url varchar(255) CHARACTER SET latin1 NOT NULL DEFAULT '',
  content longtext COLLATE utf8mb4_unicode_520_ci,
  summary text COLLATE utf8mb4_unicode_520_ci,
  article_ts bigint(20) NOT NULL DEFAULT '0' COMMENT 'published timestamp of article',
  published_date date DEFAULT NULL,
  inserted timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY UNIK (url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;`

3. Configure the database connection in the `application.properties` file.

### Run the Application with Docker:

1. Navigate to the project directory:
`cd newsscraper`
2. Build and start the Docker containers using Docker Compose:
`docker-compose up --build`

## Usage

Once the application is running, you can use the following endpoints to fetch news articles:

- Fetch Articles from RSS Feed:
`GET /news/fetch-rss`
- Fetch Articles from Index Page:
`GET /news/fetch-index`

## Contributing

We welcome contributions from everyone. To contribute to this project, please follow these steps:

1. Fork the project
2. Create a new feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to your branch (`git push origin feature/new-feature`)
5. Create a new Pull Request
  
