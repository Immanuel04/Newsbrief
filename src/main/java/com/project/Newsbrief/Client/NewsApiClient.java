package com.project.Newsbrief.Client;

import com.project.Newsbrief.Dto.NewsApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class NewsApiClient {

    private static final Logger log = LoggerFactory.getLogger(NewsApiClient.class);
    @Value("${news.api.key}")
    private String apiKey;

    @Value("${news.api.url}")
    private String baseUrl;

    @Value("${news.api.default.country}")
    private String defaultCountry;

    @Value("${news.api.url.content.top.headlines}")
    private String topHeadlinesUrl;


    private String getTopHeadlinesUrl(){
        final String baseURL = this.baseUrl + this.topHeadlinesUrl;

        final String urlwithParams = UriComponentsBuilder.fromUriString(baseURL)
                .queryParam("country", defaultCountry)
                .queryParam("apiKey", apiKey)
                .toUriString();

        log.info("Top Headline URL : {}", urlwithParams);

        return urlwithParams;
    }


    public NewsApiResponse getTopHeadLines(){
        final RestTemplate restTemplate = new RestTemplate();

        final String url = getTopHeadlinesUrl();

        NewsApiResponse result = restTemplate.getForObject(url, NewsApiResponse.class);

        log.info("Top Headlines Info : {}", result);

        return result;

    }


}
