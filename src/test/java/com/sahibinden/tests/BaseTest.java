package com.sahibinden.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.sahibinden.config.ConfigurationManager;
import com.sahibinden.driver.DriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
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
@SpringBootTest
public abstract class BaseTest {

    @Autowired
    protected ConfigurationManager config;

    @Autowired
    protected DriverManager driverManager;

    protected WebDriver driver;
    protected static ExtentReports extentReports;
    protected ExtentTest extentTest;

    @BeforeAll
    public static void setupReporting() {
        String reportPath = "reports/";
        new File(reportPath).mkdirs();

        String reportFileName = reportPath + "TestReport_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFileName);
        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
    }

    @BeforeEach
    public void setUp() {
        driver = driverManager.getDriver();
        extentTest = extentReports.createTest(getClass().getSimpleName());
    }

    @AfterEach
    public void tearDown() {
        if (driverManager != null) {
            driverManager.quitDriver();
        }
    }

    @AfterAll
    public static void tearDownAll() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }

    protected void logInfo(String message) {
        extentTest.log(Status.INFO, message);
    }

    protected void logPass(String message) {
        extentTest.log(Status.PASS, message);
    }

    protected void logFail(String message) {
        extentTest.log(Status.FAIL, message);
    }

    protected String takeScreenshot() {
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