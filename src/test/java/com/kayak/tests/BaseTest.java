package com.kayak.tests;

import com.kayak.config.ConfigurationManager;
import com.kayak.driver.DriverManager;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
})
public abstract class BaseTest {

    @Autowired
    protected ConfigurationManager config;

    @Autowired
    protected DriverManager driverManager;

    protected WebDriver driver;

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeEach
    public void setUp() {
        driver = driverManager.getDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driverManager != null) {
            driverManager.quitDriver();
        }
    }


    protected void logInfo(String message) {
        log.info(message);
        Allure.step(message);
    }


    protected void logFail(String message) {
        log.error("FAIL: " + message);
        takeScreenshot("Failure_Screenshot");
        Allure.step(message);
    }

    protected String takeScreenshot(String failureScreenshot) {
        try {
            String screenshotDir = "screenshots/";
            new File(screenshotDir).mkdirs();

            String fileName = screenshotDir + "screen_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".png";

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path path = Paths.get(fileName);
            Files.write(path, screenshot);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}