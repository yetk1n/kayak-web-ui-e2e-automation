package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class IlanDetayPage extends BasePage {

    private final By ilanTitle = By.cssSelector("div.classifiedDetailTitle > h1");
    private final By ilanPrice = By.cssSelector("span.classified-price-wrapper");


    public IlanDetayPage(WebDriver driver) {
        super(driver);
    }

    public String getIlanTitle() {
        return getText(ilanTitle);
    }

    public int getIlanPrice() {
        String priceText = getText(ilanPrice).replaceAll("[^0-9]", "");
        return Integer.parseInt(priceText);
    }
}