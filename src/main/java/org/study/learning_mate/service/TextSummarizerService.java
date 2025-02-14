//package org.study.learning_mate.service;
//
//import lombok.extern.slf4j.Slf4j;
//import okhttp3.*;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Slf4j
//@Service
//public class TextSummarizerService {
//
//    private static final String HUGGING_FACE_API_URL = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn";
//    private static final String HUGGING_FACE_API_KEY = "api=keygt";
//    private static final int MAX_TEXT_LENGTH = 1000; // 입력 텍스트 최대 길이
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
//        if (text.length() > maxLength) {
//            return text.substring(0, maxLength);
//        }
//        return text;
//    }
//
//    private static String callHuggingFaceAPI(String text) throws IOException {
//        OkHttpClient client = new OkHttpClient();
//
//        // 요청 본문 생성
//        String jsonInput = "{"
//                + "\"inputs\": \"" + text + "\","
//                + "\"parameters\": {\"max_length\": 150, \"max_new_tokens\": 50}"
//                + "}";
//
//        RequestBody body = RequestBody.create(
//                MediaType.parse("application/json"),
//                jsonInput
//        );
//
//        Request request = new Request.Builder()
//                .url(HUGGING_FACE_API_URL)
//                .post(body)
//                .addHeader("Authorization", "Bearer " + HUGGING_FACE_API_KEY)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                handleApiError(response);
//            }
//
//            String responseBody = response.body().string();
//            log.debug("Hugging Face API Response: {}", responseBody);
//
//            return parseSummaryFromResponse(responseBody);
//        }
//    }
//
//    private static void handleApiError(Response response) throws IOException {
//        String responseBody = response.body().string();
//        log.error("Hugging Face API returned an error: {}", responseBody);
//
//        if (responseBody.contains("\"error\"")) {
//            throw new IOException("API Error: " + responseBody);
//        }
//    }
//
//    private static String parseSummaryFromResponse(String responseBody) {
//        int summaryStartIndex = responseBody.indexOf("\"summary_text\":\"") + 16;
//        int summaryEndIndex = responseBody.indexOf("\"", summaryStartIndex);
//
//        if (summaryStartIndex == -1 || summaryEndIndex == -1) {
//            log.error("Failed to parse summary from response: {}", responseBody);
//            throw new IllegalStateException("Invalid API response format.");
//        }
//
//        return responseBody.substring(summaryStartIndex, summaryEndIndex);
//    }
//}
