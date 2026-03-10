package com.webcrawler;

import java.io.IOException;
import java.util.*;

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

        System.out.println(crawler.parseDocument(crawler.fetchPage("https://crawler-test.com/")));
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

        HashSet<String> visited = new HashSet<>();

        Queue<String> queue = new LinkedList<>();

        visited.add(startUrl);

        queue.offer(startUrl);

        while (!queue.isEmpty()) {
            String url = queue.poll();

            List<String> list = parseDocument(fetchPage(url));
            for (String nextUrl : list) {
                if (!visited.contains(nextUrl)) {
                    visited.add(nextUrl);
                    queue.offer(nextUrl);
                }
            }

        }
        return new ArrayList<>(visited);

    }

}
