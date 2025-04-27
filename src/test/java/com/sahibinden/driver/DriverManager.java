package com.sahibinden.driver;

import com.sahibinden.config.ConfigurationManager;
import com.sahibinden.tests.BaseTest;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DriverManager {

    @Autowired
    private ConfigurationManager config;
    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public WebDriver getDriver() {
        if (driver.get() == null) {
            initializeDriver();
        }
        return driver.get();
    }

    public void initializeDriver() {
        WebDriver webDriver;
        String browser = config.getBrowser().toLowerCase();
        boolean isRemote = config.getRemoteUrl() != null && !config.getRemoteUrl().isEmpty();

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = createStealthChromeOptions();
                if (config.isHeadless()) {
                    chromeOptions.addArguments("--headless=new");
                }
                if (isRemote) {
                    webDriver = createRemoteWebDriver(chromeOptions);
                } else {
                    // Dinamik port atanmış ChromeDriverService
                    String driverPath = System.getProperty("webdriver.chrome.driver");
                    ChromeDriverService service = new ChromeDriverService.Builder()
                            .usingDriverExecutable(new File(driverPath))
                            .usingAnyFreePort()
                            .build();
                    try {
                        service.start();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to start ChromeDriverService", e);
                    }
                    webDriver = new ChromeDriver(service, chromeOptions);
                }
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = createStealthFirefoxOptions();
                if (config.isHeadless()) {
                    firefoxOptions.addArguments("--headless");
                }
                if (isRemote) {
                    webDriver = createRemoteWebDriver(firefoxOptions);
                } else {
                    // Dinamik port atanmış GeckoDriverService
                    String geckoPath = System.getProperty("webdriver.gecko.driver");
                    GeckoDriverService geckoService = new GeckoDriverService.Builder()
                            .usingDriverExecutable(new File(geckoPath))
                            .usingAnyFreePort()
                            .build();
                    try {
                        geckoService.start();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to start GeckoDriverService", e);
                    }
                    webDriver = new FirefoxDriver(geckoService, firefoxOptions);
                }
                break;

            case "edge":
                if (isRemote) {
                    EdgeOptions edgeOptions = createStealthEdgeOptions();
                    webDriver = createRemoteWebDriver(edgeOptions);
                } else {
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = createStealthEdgeOptions();
                    webDriver = new EdgeDriver(edgeOptions);
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        // Genel ayarlar
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getTimeout()));
        if (!config.isMobileView()) {
            webDriver.manage().window().maximize();
        }

        // Selenoid için stealth script
        if (webDriver instanceof RemoteWebDriver) {
            ((RemoteWebDriver) webDriver).executeScript(
                    "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});" +
                            "Object.defineProperty(navigator, 'languages', {get: () => ['en-US', 'en', 'es']});" +
                            "Object.defineProperty(navigator, 'plugins', {get: () => [1, 2, 3, 4, 5]});"
            );
        }

        driver.set(webDriver);
        log.info("Creating WebDriver instance for thread: {}", Thread.currentThread().getId());
    }

    private ChromeOptions createStealthChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-extensions");

        options.setCapability("selenoid:options", Map.of(
                "enableVNC", true,
                "screenResolution", "1920x1080x24"
        ));

        if (config.isMobileView()) {
            Map<String, Object> deviceMetrics = new HashMap<>();
            deviceMetrics.put("width", 390);
            deviceMetrics.put("height", 844);
            deviceMetrics.put("pixelRatio", 3.0);

            Map<String, Object> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceMetrics", deviceMetrics);
            mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) " +
                    "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 SahibindenOtomasyon/da1f7dbf5c7842819cb75d6a25362611");

            options.setExperimentalOption("mobileEmulation", mobileEmulation);
            options.setCapability("selenoid:options", Map.of(
                    "enableVideo", true,
                    "enableVNC", true
            ));
        } else {
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                    "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 SahibindenOtomasyon/da1f7dbf5c7842819cb75d6a25362611");
        }

        return options;
    }

    private FirefoxOptions createStealthFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-profile");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        options.addPreference("dom.webdriver.enabled", false);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("general.useragent.override",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:125.0) Gecko/20100101 Firefox/125.0 SahibindenOtomasyon");
        return options;
    }

    private EdgeOptions createStealthEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0 SahibindenOtomasyon");
        return options;
    }

    private WebDriver createRemoteWebDriver(Object options) {
        try {
            return new RemoteWebDriver(new URL(config.getRemoteUrl()),
                    (org.openqa.selenium.remote.AbstractDriverOptions<?>) options);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Remote WebDriver URL", e);
        }
    }

    public void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
