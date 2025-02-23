//package org.study.learning_mate.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//@Slf4j
//@Service
//public class TextSummarizerService {
//
//    private static final String HUGGING_FACE_API_URL = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";
//    private static final int MAX_TEXT_LENGTH = 1000; // 입력 텍스트 최대 길이
//    private static final int MAX_TOKENS = 500; // Hugging Face 모델이 허용하는 최대 토큰 수
//
//    private final RestTemplate restTemplate;
//    private final String huggingFaceApiKey;
//
//    public TextSummarizerService(RestTemplate restTemplate, @Value("${huggingface.api.key}") String huggingFaceApiKey) {
//        this.restTemplate = restTemplate;
//        this.huggingFaceApiKey = huggingFaceApiKey;
//    }
//
//    public String summarize(String url) {
//        log.info("Starting summarization for URL: {}", url);
//        String result = "";
//        try {
//            // HTML 콘텐츠 가져오기
//            log.info("Connecting to URL...");
//            Document document = Jsoup.connect(url).timeout(10000).get();
//
//            // 주요 텍스트 추출
//            log.info("Extracting text from the document...");
//            String extractedText = extractText(document);
//            log.debug("Extracted Text: {}", extractedText);
//
//            // 입력 텍스트 길이 제한
//            extractedText = truncateText(extractedText, MAX_TEXT_LENGTH);
//
//            // 토큰 수 확인 및 텍스트 조정
//            int tokenCount = getTokenCount(extractedText);
//            log.debug("Truncated Text: {}", extractedText);
//            log.info("Text Length : {}", extractedText.length());
//            log.info("Token count before adjustment: {}", tokenCount);
//
//            if (tokenCount > MAX_TOKENS) {
//                extractedText = adjustTextToMaxTokens(extractedText, MAX_TOKENS);
//                tokenCount = getTokenCount(extractedText);
//                log.warn("Text adjusted to meet token limit. New token count: {}", tokenCount);
//            }
//
//            // AI 모델로 요약
//            log.info("Calling Hugging Face API for summarization...");
//            result = callHuggingFaceAPI(extractedText);
//            log.debug("Summary Result: {}", result);
//
//        } catch (IOException e) {
//            log.error("Error occurred while processing the URL: {}", e.getMessage(), e);
//        } catch (Exception e) {
//            log.error("Unexpected error occurred: {}", e.getMessage(), e);
//        }
//        return result;
//    }
//
//    private static String extractText(Document document) {
//        Elements elements = document.select("h1, h2, p");
//        StringBuilder content = new StringBuilder();
//
//        for (Element element : elements) {
//            content.append(element.text()).append("\n");
//        }
//
//        return content.toString();
//    }
//
//    private static String truncateText(String text, int maxLength) {
//        log.info("Truncating text");
//        if (text.length() > maxLength) {
//            return text.substring(0, maxLength);
//        }
//        return text;
//    }
//
//    private String callHuggingFaceAPI(String text) {
//        // 요청 본문 생성
//
//        String requestBody = String.format(
//                "{\"inputs\": \"%s\", \"parameters\": {\"max_length\": 1024, \"truncation\": true}}",
//                text
//        );
//
//
//        // 헤더 설정
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(huggingFaceApiKey);
//
//        // 요청 엔티티 생성
//        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(
//                    HUGGING_FACE_API_URL,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                return parseSummaryFromResponse(response.getBody());
//            } else {
//                log.error("Hugging Face API returned status code: {}", response.getStatusCode());
//                throw new IllegalStateException("Failed to get a valid response from Hugging Face API.");
//            }
//        } catch (Exception e) {
//            log.error("Error while calling Hugging Face API: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to summarize text using Hugging Face API.");
//        }
//    }
//
//    private static String parseSummaryFromResponse(String responseBody) {
//        try {
//            int summaryStartIndex = responseBody.indexOf("\"summary_text\":\"") + 16;
//            int summaryEndIndex = responseBody.indexOf("\"", summaryStartIndex);
//
//            if (summaryStartIndex == -1 || summaryEndIndex == -1) {
//                log.error("Failed to parse summary from response: {}", responseBody);
//                throw new IllegalStateException("Invalid API response format.");
//            }
//
//            return responseBody.substring(summaryStartIndex, summaryEndIndex);
//        } catch (Exception e) {
//            log.error("Error parsing the summary from the response body.", e);
//            throw new RuntimeException("Failed to parse the summary from the Hugging Face API response.");
//        }
//    }
//
//    // 토큰 수 계산
//    private static int getTokenCount(String text) {
//        return text.split("\\s+").length; // 공백을 기준으로 분리하여 단어 수 계산
//    }
//
//    // 텍스트를 최대 토큰 수에 맞게 조정
//    private static String adjustTextToMaxTokens(String text, int maxTokens) {
//        String[] words = text.split("\\s+");
//        if (words.length <= maxTokens) {
//            return text;
//        }
//        return String.join(" ", Arrays.copyOfRange(words, 0, maxTokens));
//    }
//
//}
