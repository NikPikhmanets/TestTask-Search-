package com.testtask.task.service;


import com.testtask.task.model.DataOfPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.USER_AGENT;

@Component
public class TextExtractor {

    private final Logger logger = LogManager.getLogger(TextExtractor.class);

    public Optional<DataOfPage> getDataOfPage(String url) {

        try {
            String page = getPage(url);

            if (page == null) {
                return Optional.empty();
            }
            DataOfPage dataOfPage = new DataOfPage(url);
            Document document = Jsoup.parse(page);

            String title = document.title();
            dataOfPage.setTitle(title);

            String body = document.text();
            dataOfPage.setBody(body);

            Elements elements = document.select("a[href]");
            Set<String> list = elements.stream()
                    .map(element -> element.attr("href"))
                    .filter(href -> !href.isEmpty())
                    .filter(this::isValidUrl)
                    .collect(Collectors.toSet());
            dataOfPage.setUrls(list);

            return Optional.of(dataOfPage);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();

            return Optional.empty();
        }
    }

    private Boolean isValidUrl(String url) {
        String regex = "(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }

    private String getPage(String url) throws IOException, InterruptedException {
        logger.info(url);
        URI uri = getUri(url);
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header(USER_AGENT, "Apache HTTPClient")
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> send = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return send.body();
    }

    private URI getUri(String url) {
        return URI.create(url);
    }
}
