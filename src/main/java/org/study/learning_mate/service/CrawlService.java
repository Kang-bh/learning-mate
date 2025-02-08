package org.study.learning_mate.service;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        WebDriver webDriver = new ChromeDriver();
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
