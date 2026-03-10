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

        Elements urls = document.select("a[href]:not([rel=nofollow])");

        for (Element url : urls) {
            links.add(url.attr("abs:href"));
        }

        return links;
    }

    public List<String> crawl(String startUrl) {

        Set<String> visited = ConcurrentHashMap.newKeySet();

        Queue<String> queue = new ConcurrentLinkedQueue<>();

        AtomicInteger activeTasks = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        visited.add(startUrl);

        queue.offer(startUrl);

        activeTasks.incrementAndGet();

        while (activeTasks.get() > 0) {
            String url = queue.poll();

            if (url != null) {
                executor.submit(() -> {
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

                    } finally {
                        activeTasks.decrementAndGet();
                    }
                });
            }

        }
        executor.shutdown();
        return new ArrayList<>(visited);

    }

}
