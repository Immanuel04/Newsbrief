package com.project.Newsbrief.Service;

import com.project.Newsbrief.Client.NewsApiClient;
import com.project.Newsbrief.Client.OllamaClient;
import com.project.Newsbrief.Dto.NewsApiResponse;
import com.project.Newsbrief.Dto.NewsSummaryResponse;
import com.project.Newsbrief.Dto.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NewsBriefService {

    private static final Logger log = LoggerFactory.getLogger(NewsBriefService.class);
    public final NewsApiClient newsApiClient;
    private final OllamaClient ollamaClient;

    @Autowired
    public NewsBriefService(NewsApiClient newsApiClient, OllamaClient ollamaClient){
        this.newsApiClient = newsApiClient;
        this.ollamaClient = ollamaClient;
    }


    @Cacheable(value = "newsBriefCache", key = "#root.method.name")
    public NewsSummaryResponse generateGeneralNewsBrief(boolean isRender){
        final NewsApiResponse newsApiResponse = newsApiClient.getTopHeadLines();

        log.info("Requesting summary for {} articles", newsApiResponse.getArticles().size());

        final OllamaResponse ollamaResponse = ollamaClient.generateSummary(newsApiResponse.getArticles(), isRender);

        return NewsSummaryResponse.builder()
                .createdAt(java.time.LocalDateTime.now())
                .summary(ollamaResponse.getResponse())
                .build();
    }
}
