package com.kayak.tests;

import com.kayak.pages.FlightDetailsPage;
import com.kayak.pages.FlightResultsPage;
import com.kayak.pages.FlightSearchPage;
import com.kayak.pages.HomePage;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Epic("Flight Booking")
@Feature("Flight Search and Selection")
public class FlightBookingTest extends BaseTest {

    @Test
    @Description("Search for flights and verify details")
    @Severity(SeverityLevel.CRITICAL)
    public void testFlightBooking() throws InterruptedException {
        logInfo("Starting flight booking test");

        // Navigate to homepage
        HomePage homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getBaseUrl());
        homePage.acceptCookiesIfPresent();
        logInfo("Navigated to Kayak homepage");

        // Enter flight search details
        FlightSearchPage flightSearchPage = new FlightSearchPage(driver);
        flightSearchPage.enterOriginAirport("DOHA");
        logInfo("Entered origin airport: DOHA");

        flightSearchPage.enterDestinationAirport("NRT");
        logInfo("Entered destination airport: NRT");

        flightSearchPage.selectDates("7 Mayıs 2025", "13 Mayıs 2025");
        logInfo("Selected travel dates: 28 Mayıs 2025 - 4 Haziran 2025");

        flightSearchPage.selectPassengers(2, 1, 1);
        logInfo("Selected passengers: 2 adults, 1 student, 1 child");

        // Search flights
        FlightResultsPage resultsPage = flightSearchPage.searchFlights();
        resultsPage.waitForResultsToLoad();
        logInfo("Flight search completed");

        // Apply filters
        resultsPage.selectBookOnKayakFilter();
        logInfo("Applied 'Select book on KAYAK' filter");

        resultsPage.selectQuickestFilter();
        logInfo("Applied 'Quickest' filter");

        resultsPage.loadMoreResults();
        logInfo("Loaded more flight results");

        // Find outbound flight
        resultsPage.waitForResultsToLoad();
        WebElement outboundFlight = resultsPage.findCheapestOneAfternoonFlight();
        assertNotNull(outboundFlight, "Should find an outbound flight matching criteria");
        String outboundPrice = resultsPage.getFlightPriceWithRetry(outboundFlight);

        logInfo("Found outbound flight with price: " + outboundPrice);


        // Select flight and verify details
        FlightDetailsPage detailsPage = resultsPage.goFoundFlightPage(outboundFlight);
        detailsPage.isFlightDetailPageLoaded();
        assertTrue(detailsPage.isFlightDetailPageLoaded(), "Flight details page should load");

        // Verify flight details
        assertTrue(detailsPage.verifyFlightDates("7 May", "13 May"),
                "Flight dates should match selected dates");
        logInfo("Verified flight dates match");

        assertEquals(4, detailsPage.getTotalPassengerCount(),
                "Passenger count should match selected count (2 adults + 1 student + 1 child)");
        logInfo("Verified passenger count matches");

        assertTrue(detailsPage.hasOneStop(), "Flight should have 1 stop");
        logInfo("Verified flight has one stop");

        String detailsPrice = detailsPage.getPrice();
        assertEquals(outboundPrice, detailsPrice, "Price in results should match price in details");
        logInfo("Verified flight price matches between results and details page");
    }
}