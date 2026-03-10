package com.webcrawler;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        SimpleCrawler crawler = new SimpleCrawler();

        System.out.println(crawler.fetchPage("https://crawler-test.com/"));
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
}
