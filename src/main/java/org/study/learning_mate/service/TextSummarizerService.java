package org.study.learning_mate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TextSummarizerService {

    private final RestTemplate restTemplate;
    private final OpenAPI api;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    public String summarizeUrl(String url) {
        try {
            // 1. URL에서 HTML 내용 추출
            Document document = Jsoup.connect(url).get();
            String content = extractText(document);

            // 2. 텍스트 길이 제한 (900자)
            content = truncateText(content, 900);

            // 3. Gemini API 호출
            return callGeminiApi(content);
        } catch (Exception e) {
            e.printStackTrace();
            return "요약에 실패했습니다.";
        }
    }

    private String callGeminiApi(String text) {
        // 요청 본문 생성 (한국어 요약 요청)
        String requestBody = String.format(
                "{\"contents\": [{\"parts\": [{\"text\": \"다음 내용을 한국어로 간결하게 요약해 주세요:\\n%s\"}]}]}",
                text.replace("\"", "\\\"")
        );

        String apiUrlWithKey = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrlWithKey,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return extractSummaryFromResponse(response.getBody());
            } else {
                throw new IllegalStateException("Gemini API 응답 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Gemini API 호출 중 오류가 발생했습니다.";
        }
    }

    private String extractSummaryFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 응답에서 요약된 텍스트 추출
            JsonNode summaryNode = rootNode.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0).path("text");

            if (summaryNode != null && !summaryNode.isMissingNode()) {
                return summaryNode.asText();
            } else {
                throw new IllegalStateException("응답에서 요약 텍스트를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "응답 파싱 중 오류가 발생했습니다.";
        }
    }

    private String extractText(Document document) {
        // HTML 문서에서 <p>, <h1>, <h2> 태그의 텍스트만 추출
        return document.select("p, h1, h2").text();
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength);
        }
        return text;
    }
}
