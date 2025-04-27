package com.sahibinden.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MobileServislerPage extends BasePage {

    private final By oto360LinkMobile = By.cssSelector("a.auto-360.ios[href='https://www.sahibinden.com/oto360']");
    private final By satarkenItemMobile = By.xpath("//p[@class='title' and text()='Satarken']");
    private final By aracDegerlemeLinkMobile = By.xpath("//span[@class='title' and text()='Araç Değerleme']");


    public MobileServislerPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToMobileAracDegerlemePage() {
        click(oto360LinkMobile);
        waitForElementVisible(satarkenItemMobile);
        click(satarkenItemMobile);
        waitForElementVisible(aracDegerlemeLinkMobile);
        click(aracDegerlemeLinkMobile);
    }
}
