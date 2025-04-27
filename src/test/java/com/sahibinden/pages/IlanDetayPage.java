package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IlanDetayPage extends BasePage {

    private final By ilanTitle = By.cssSelector("div.classifiedDetailTitle > h1");
    private final By ilanTitleMobile = By.className("classified-title");

    private final By ilanPrice = By.cssSelector("span.classified-price-wrapper");
    private final By ilanPriceMobile = By.cssSelector("span.price");

    private final By priceHistoryIcon = By.id("splash-m-price-history-icon");


    public IlanDetayPage(WebDriver driver) {
        super(driver);
    }

    public String getIlanTitle() {
        String titleText;
        if (isMobileView()) {
            titleText = getText(ilanTitleMobile);
        } else {
            titleText = getText(ilanTitle);
        }
        return titleText;
    }

    public int getIlanPrice() {
        String priceText;
        if (isMobileView()) {
            priceText = getText(ilanPriceMobile).replaceAll("[^0-9]", "");
        } else {
            priceText = getText(ilanPrice).replaceAll("[^0-9]", "");
        }
        return Integer.parseInt(priceText);
    }

    public void closeFavoritePopup() {
//        By closeButton = By.cssSelector("div[class*='popup-close']");
//        if (isElementDisplayed(closeButton)) {
//            click(closeButton);
//        }
        if (isElementDisplayed(priceHistoryIcon)) {
            click(priceHistoryIcon);
        }
    }
}