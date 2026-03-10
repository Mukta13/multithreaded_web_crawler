package com.webcrawler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        SimpleCrawler crawler = new SimpleCrawler();

        crawler.crawl("https://crawler-test.com/");
    }

}

class SimpleCrawler {
    public SimpleCrawler() {

    }

    public Document fetchPage(String startUrl) {
        Document document = null;
        try {
            document = Jsoup.connect(startUrl).get();

        } catch (Exception e) {

            System.err.println("Error Fetching the url: " + startUrl);

            return null;
        }

        return document;
    }

    public List<String> parseDocument(Document document) {

        ArrayList<String> links = new ArrayList<String>();

        Elements urls = document.select("a[href]:not([rel=nofollow])"); // rel - nofollow means to not extract this url
                                                                        // for webcrawling

        for (Element url : urls) {
            links.add(url.attr("abs:href"));
        }

        return links;
    }

    public List<String> crawl(String startUrl) {

        Set<String> visited = ConcurrentHashMap.newKeySet(); // no concurrent hash set thus use concurrent hashmap as
                                                             // hash set

        Queue<String> queue = new ConcurrentLinkedQueue<>();

        AtomicInteger activeTasks = new AtomicInteger(0); // for concurrent thread safety do not use int directly

        ExecutorService executor = Executors.newFixedThreadPool(10); // creates a fixed thread pool of 10 threads so
                                                                     // that we can crawl concurrently

        visited.add(startUrl);

        queue.offer(startUrl);

        activeTasks.incrementAndGet();

        while (activeTasks.get() > 0) {
            String url = queue.poll();

            if (url != null) { // need this check to avoid NPE if multithreads access the last element in the
                               // queue in a short interval of time
                executor.submit(() -> { // submit the task to the executor service
                    try {
                        System.out.println(Thread.currentThread().getName() + " is fetching: " + url);
                        List<String> list = parseDocument(fetchPage(url));
                        for (String nextUrl : list) {
                            if (!visited.contains(nextUrl)) {
                                visited.add(nextUrl);
                                queue.offer(nextUrl);
                                activeTasks.incrementAndGet();
                            }
                        }

                    } finally { // finally block to ensure that the activeTasks counter is decremented even if
                                // an exception occurs
                        activeTasks.decrementAndGet();
                    }
                });
            }

        }
        executor.shutdown();
        return new ArrayList<>(visited);

    }

}
