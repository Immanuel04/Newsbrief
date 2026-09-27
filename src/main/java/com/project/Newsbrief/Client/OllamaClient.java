package com.project.Newsbrief.Client;


import com.project.Newsbrief.Dto.Article;
import com.project.Newsbrief.Dto.OllamaRequest;
import com.project.Newsbrief.Dto.OllamaResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class OllamaClient {
    private static final Logger log = LoggerFactory.getLogger(OllamaClient.class);
    @Value("${ollama.base.url}")
    private String ollamabaseurl;

    @Value("${ollama.mistral.model}")
    private String aiModel;

    public OllamaResponse generateSummary(final List<Article> articles, final boolean isRender){
        final RestTemplate restTemplate = new RestTemplate();

        final String prompt = getPrompt(articles, isRender);

        final OllamaRequest requestPayload = OllamaRequest.builder()
                .model(aiModel)
                .prompt(prompt)
                .stream(false)
                .build();

        final HttpEntity<OllamaRequest> entity = getHttpEntity(requestPayload);

        final ResponseEntity<OllamaResponse> response = restTemplate.postForEntity(ollamabaseurl, entity, OllamaResponse.class);
        OllamaResponse ollamaResponse = response.getBody();
        // Clean the response only when HTML is requested
        if (ollamaResponse != null && isRender) {
            ollamaResponse.setResponse(
                    cleanHtmlResponse(ollamaResponse.getResponse())
            );
        }
        log.info("Ollama response : {}", ollamaResponse);

        return ollamaResponse;

    }

    private static HttpEntity<OllamaRequest> getHttpEntity(OllamaRequest requestPayload){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OllamaRequest> entity = new HttpEntity<>(requestPayload, httpHeaders);
        return entity;
    }

    private String getPrompt (List<Article> articles, final boolean isRender){
        final StringBuilder promptBuilder = new StringBuilder();

        if(isRender) {
            promptBuilder.append("""
            You are an expert HTML generator.

            Your task is to create a complete HTML webpage from the news articles provided below.
            From the news articles provided below, identify the top 10 most important
            and relevant news stories.

            STRICT RULES:
                            1. Return ONLY valid HTML.
                            2. Your response MUST start with <html> and end with </html>.
                            3. Do NOT write any explanation before or after the HTML.
                            4. Do NOT use Markdown.
                            5. Do NOT use ```html or ``` code fences.
                            6. Do NOT say "Here is the HTML".
                            7. Do NOT explain the HTML.
                            8. Use inline CSS or a <style> section.
                            9. Display ONLY the selected top 10 articles.
                            10. Do not omit, combine, or duplicate the selected articles.
                            11. Do not use placeholders such as "<!-- More articles omitted -->".
                            12. Each article must have a clear title and description.
                            13. If fewer than 10 articles are provided, display all available articles.
                            14. The final response must be directly renderable by a browser.

            Return ONLY the HTML document.

            """);
        }else{
                    promptBuilder.append("You are a news summarizer. " +
                            "Summarize the top global news stories from today in a concise and informative way. " +
                            "Focus on major events, political development, economic updates, and major technology or " +
                            "science breakthrough. Keep the summary clear, objective, and easy to read, like a daily news brief.");
        }


        for (Article article : articles){
            promptBuilder.append("Title : ").append(article.getTitle()).append("\n")
                    .append("Description : ").append(article.getDescription()).append("\n")
                    .append("End of article\n\n");
        }

        final String prompt = promptBuilder.toString();
        return prompt;



    }
    private String cleanHtmlResponse(String html) {

        if (html == null || html.isBlank()) {
            return html;
        }

        html = html.trim();

        // Remove Markdown code fences
        html = html.replace("```html", "")
                .replace("```HTML", "")
                .replace("```", "")
                .trim();

        // Find the actual HTML document
        int htmlStart = html.indexOf("<html");
        int htmlEnd = html.lastIndexOf("</html>");

        if (htmlStart >= 0 && htmlEnd >= 0) {

            html = html.substring(
                    htmlStart,
                    htmlEnd + "</html>".length()
            );
        }

        return html;
    }
}
