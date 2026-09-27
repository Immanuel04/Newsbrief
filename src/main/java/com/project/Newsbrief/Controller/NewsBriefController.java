package com.project.Newsbrief.Controller;

import com.project.Newsbrief.Dto.NewsSummaryResponse;
import com.project.Newsbrief.Service.NewsBriefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
@RequestMapping("/api/v1/news-brief")
public class NewsBriefController {

    private final NewsBriefService newsBriefService;

    @Autowired
    public NewsBriefController(NewsBriefService newsBriefService) {

        this.newsBriefService = newsBriefService;
    }


    @GetMapping(value = "/general-brief", produces = MediaType.APPLICATION_JSON_VALUE)
    public NewsSummaryResponse GeneralBrief() {
        return newsBriefService.generateGeneralNewsBrief(false);
    }


    @GetMapping(value = "/general-brief/render", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> GeneralBriefUI() {
        final NewsSummaryResponse newsSummaryResponse =
                newsBriefService.generateGeneralNewsBrief(true);

        final String htmlContent = newsSummaryResponse
                .getSummary().substring(7, newsSummaryResponse.getSummary().length() - 3);

        System.out.println("HTML CONTENT:");
        System.out.println(htmlContent);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }

}


