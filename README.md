# Multithreaded Web Crawler

A simple multithreaded web crawler written in Java. It systematically browses the World Wide Web from a starting URL, extracting all the links present on the pages, and visits those links recursively while ensuring thread safety and efficiency.

## Features

- **Concurrent Execution:** Utilizes Java's `ExecutorService` to manage a fixed thread pool, allowing multiple web pages to be fetched and processed simultaneously.
- **Thread Safety:** Employs concurrent data structures like `ConcurrentHashMap.newKeySet()` for keeping track of visited URLs and `ConcurrentLinkedQueue` for pending URLs to crawl, preventing race conditions.
- **HTML Parsing:** Leverages [Jsoup](https://jsoup.org/) to fetch and parse HTML pages conveniently.
- **Respects `nofollow`:** It intentionally avoids extracting links formatted with `rel=nofollow`.

## Requirements

- **Java Development Kit (JDK):** Version 21 (as defined in `pom.xml`)
- **Maven:** For dependency management and building the project

## Dependencies

- [Jsoup](https://jsoup.org/) (Version 1.22.1): Used for parsing HTML to extract links from pages.
- [JUnit](https://junit.org/) (Version 4.11): Used for testing.

## Getting Started

### 1. Clone or Download the project
Ensure you have the project source code on your local machine.

### 2. Build the project
To compile the project and download the required dependencies (like Jsoup) using Maven, navigate to the root directory of the project (where `pom.xml` is located) and run:

```bash
mvn clean install
```

### 3. Run the application
You can run the application directly from your IDE or by executing the main class using Maven.

Using Maven's exec plugin (you might need to ensure this is appropriately configured, or use the `java` command if compiling manually):

```bash
mvn exec:java -Dexec.mainClass="com.webcrawler.App"
```

## How It Works

1. The execution starts in the `App.main()` method, initializing a `SimpleCrawler` instance.
2. The crawler starts at a specified URL (e.g., `https://crawler-test.com/`).
3. An `ExecutorService` acts as a thread pool to handle concurrent tasks.
4. Each task fetches a webpage using Jsoup, parses it to find all valid links (`a[href]:not([rel=nofollow])`).
5. These new links are checked against a globally shared thread-safe set (`visited`). If a link hasn't been visited before, it is added to the work queue to be crawled by an available thread.
6. The application tracks active tasks utilizing an `AtomicInteger` to safely coordinate the main thread until all crawling tasks are completed.
