package com.kayak.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By cookiesAcceptButton = By.xpath("//div[text()='Tümünü kabul et' and contains(@class, 'RxNS-button-content')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void navigateToHomePage(String baseUrl) {
        driver.get(baseUrl);
        waitForPageLoad();
    }

    public void acceptCookiesIfPresent() {
        if (isElementDisplayed(cookiesAcceptButton)) {
            click(cookiesAcceptButton);
        }
    }
}