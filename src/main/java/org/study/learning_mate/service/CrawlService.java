package org.study.learning_mate.service;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CrawlService {

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

    public String crawlLectureTitle(String url) {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver-linux64");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 브라우저 창을 띄우지 않음
        options.addArguments("--no-sandbox"); // 샌드박스 비활성화
        options.addArguments("--disable-dev-shm-usage"); // /dev/shm 사용량 줄임
        options.addArguments("--disable-gpu"); // GPU 비활성화 (Linux 환경에서 필요)
        options.addArguments("--window-size=1920,1080"); // 브라우저 창 크기 설정

        WebDriver webDriver = new ChromeDriver(options);
        WebDriverWait driverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        try {
            System.out.println("url + " + url);
            webDriver.get(url);

            String title = webDriver.getTitle();

            System.out.println("title : " + title);

            return title;
        } finally { // TODO :: Error Catch
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }
}
