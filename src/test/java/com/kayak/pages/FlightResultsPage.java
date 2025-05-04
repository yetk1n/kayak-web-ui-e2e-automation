package com.kayak.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class FlightResultsPage extends BasePage {

    private final By loadingIndicator =  By.xpath("//div[@role='progressbar']");
    private final By bookOnKayakFilter = By.xpath("//div[@role='button' and @aria-label='KAYAK üzerinden rezerve edin']");
    private final By bookOnKayakFilterCheckbox = By.cssSelector("label[for='valueSetFilter-vertical-whisky-whisky']");
    private final By quickestFilter = By.cssSelector("div[aria-label='En kısa']");
    private final By showMoreResultsButton = By.xpath("//div[@role='button' and text()='Daha fazla sonuç']");
    private final By flightCards = By.id("flight-results-list-wrapper");
    private final By stopsElement = By.xpath(".//span[contains(@class, 'JWEO-stops-text')]");
    private final By departureBlock = By.cssSelector("div.vmXl.vmXl-mod-variant-large");
    private final By departureTime = By.xpath(".//span[1]");
    private final By priceElements = By.cssSelector(".e2GB-price-text");
    private final By flightOptions = By.cssSelector(".nrc6");



    public FlightResultsPage(WebDriver driver) {
        super(driver);
    }

    public void waitForResultsToLoad() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingIndicator));
            wait.until(ExpectedConditions.visibilityOfElementLocated(flightCards));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(flightOptions));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(stopsElement));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(departureBlock));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(departureTime));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(priceElements));

        } catch (Exception e) {
            // Sometimes the loading indicator disappears quickly
        }

        // Wait for flight cards to appear
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(flightCards));
    }

    public void selectBookOnKayakFilter() {
        waitForResultsToLoad();
        scrollToElement(bookOnKayakFilter);
        click(bookOnKayakFilter);
        click(bookOnKayakFilterCheckbox);
        scrollToTheTop();
        waitForResultsToLoad();
    }

    public void selectQuickestFilter() {
        waitForResultsToLoad();
        click(quickestFilter);
        waitForResultsToLoad();
    }

    public void specificScrollingForMoreResults(By locator) throws InterruptedException {
        WebElement element = waitForElementVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, -100);"); // Adjust offset as needed
        Thread.sleep(400);
    }

    public void loadMoreResults() throws InterruptedException {
        waitForResultsToLoad();
        if(isElementDisplayed(showMoreResultsButton)) {
            specificScrollingForMoreResults(showMoreResultsButton);
            waitForElementClickable(showMoreResultsButton);
            click(showMoreResultsButton);
            waitForResultsToLoad();
            Thread.sleep(400);
            if(isElementDisplayed(showMoreResultsButton)){
                specificScrollingForMoreResults(showMoreResultsButton);
                waitForElementClickable(showMoreResultsButton);
                click(showMoreResultsButton);
                waitForResultsToLoad();
            }
        }
    }

    public WebElement findCheapestOneAfternoonFlight() throws InterruptedException {
        Thread.sleep(5000);
        List<WebElement> allFlights = driver.findElements(flightOptions);
        System.out.println("Total flights found: " + allFlights.size());

        return findCheapestOneStopAfterNoonFlight(allFlights);
    }

    private WebElement findCheapestOneStopAfterNoonFlight(List<WebElement> allFlights) throws InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        List<WebElement> filteredFlights = allFlights.stream()
                .filter(flight -> {
                    try {
                        // find all "stops" spans inside this flight element
                        List<WebElement> stops = flight.findElements(stopsElement);
                        // must have at least two legs reported
                        if (stops.size() < 2) return false;
                        // each leg must contain exactly "1 aktarma"
                        return stops.stream().allMatch(e -> e.getText().contains("1 aktarma"));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .filter(flight -> {
                    try {
                        Thread.sleep(300);
                        WebDriverWait flightWait = new WebDriverWait(driver, Duration.ofSeconds(2));
                        WebElement departureBlockElement = flightWait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(flight, departureBlock));
                        WebElement departureTimeElement = flightWait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(departureBlockElement, departureTime));

                        String departureText = departureTimeElement.getText();
                        LocalTime time = LocalTime.parse(departureText, formatter);
                        return time.isAfter(LocalTime.NOON);
                    } catch (Exception e) {
                        System.out.println("Flight skipped due to missing departure time: " + e.getMessage());
                        return false;
                    }
                })
                .toList();

        // Handle the case where no "1 aktarma" flights are found
        if (filteredFlights.isEmpty()) {
            System.out.println("No flights found matching criteria. Proceeding with default logic.");
            return null;
        }

        if (filteredFlights.size() == 1) {
            return filteredFlights.get(0);
        }

        //scroll to first flight
        scrollToWebElement(filteredFlights.get(0));

        return filteredFlights.stream()
                .min(Comparator.comparingInt(flight -> {
                    try {
                        String priceText = flight.findElement(By.cssSelector(".e2GB-price-text"))
                                .getText()
                                .replaceAll("[^\\d]", "");
                        return Integer.parseInt(priceText);
                    } catch (Exception e) {
                        return Integer.MAX_VALUE;
                    }
                }))
                .orElse(null); // null if no matching flight found
    }

    public String getFlightPriceWithRetry(WebElement flightCard) {
        int retries = 3;
        while (retries > 0) {
            try {
                // Re-locate the flight card to avoid stale references
                List<WebElement> allFlights = driver.findElements(flightOptions);
                WebElement finalFlightCard = flightCard;
                flightCard = allFlights.stream()
                        .filter(flight -> flight.equals(finalFlightCard))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Flight card not found in DOM"));

                // Re-locate the price element within the flight card
                WebElement priceElement = wait.until(ExpectedConditions.visibilityOf(
                        flightCard.findElement(By.cssSelector(".e2GB-price-text"))
                ));
                return priceElement.getText();
            } catch (StaleElementReferenceException e) {
                retries--;
                System.out.println("StaleElementReferenceException encountered. Retrying... Remaining attempts: " + retries);
            } catch (Exception e) {
                retries--;
                System.out.println("Exception encountered: " + e.getMessage() + ". Retrying... Remaining attempts: " + retries);
            }
        }
        throw new RuntimeException("Failed to fetch flight price after retries");
    }


    public FlightDetailsPage goFoundFlightPage(WebElement outboundFlight) {
        scrollToWebElement(outboundFlight);
        waitForWebElementVisible(outboundFlight);
        outboundFlight.click();
        waitForPageLoad();
        return new FlightDetailsPage(driver);
    }

}