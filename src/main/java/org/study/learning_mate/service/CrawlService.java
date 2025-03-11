package org.study.learning_mate.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.LambdaClientBuilder;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
public class CrawlService {


    private final LambdaClient lambdaClient;
    private final ObjectMapper objectMapper;

    public CrawlService(
            @Value("${spring.cloud.aws.s3.bucket.credentials.accessKey}") String accessKey,
            @Value("${spring.cloud.aws.s3.bucket.credentials.secretKey}") String secretKey
    ) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        this.lambdaClient = LambdaClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String invokeLambda(String url) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of("url", url));

            InvokeRequest request = InvokeRequest.builder()
                    .functionName("crawl-lecture")
                    .payload(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeResponse response = lambdaClient.invoke(request);
            String responseBody = StandardCharsets.UTF_8.decode(response.payload().asByteBuffer()).toString();

            // Lambda 응답을 파싱하여 title 추출
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode bodyNode = objectMapper.readTree(rootNode.get("body").asText());
            String title = bodyNode.get("title").asText();

            return title;
        } catch (LambdaException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//     public CrawlService() {
//         WebDriverManager.chromedriver().setup();
//     }
// TODO:  필요없는 경우 블로그 포스팅
//     public CrawlService(
//             WebDriver webDriver,
//             WebDriverWait driverWait
//     ) {
//         this.webDriver = webDriver;
//         this.driverWait = driverWait;
//     }

//    public String crawlLectureTitle(String url) {
////        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver-linux64/chromedriver");
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless"); // 브라우저 창을 띄우지 않음
//        options.addArguments("--no-sandbox"); // 샌드박스 비활성화
//        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용량 줄임
//        options.addArguments("--disable-gpu"); // GPU 비활성화 (Linux 환경에서 필요)
//
//        WebDriver webDriver = new ChromeDriver(options);
//        System.out.println("22222 + ");
//        WebDriverWait driverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
//
//        try {
//            System.out.println("url + " + url);
//            webDriver.get(url);
//
//            String title = webDriver.getTitle();
//
//            System.out.println("title : " + title);
//
//            return title;
//        } finally { // TODO :: Error Catch
//            if (webDriver != null) {
//                webDriver.quit();
//            }
//        }
//    }
}
