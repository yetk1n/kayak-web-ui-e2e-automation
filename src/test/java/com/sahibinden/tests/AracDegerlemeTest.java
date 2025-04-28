package com.sahibinden.tests;

import com.sahibinden.config.MobileTestExtension;
import com.sahibinden.pages.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MobileTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Epic("Araç Değerleme")
@Feature("Araç Fiyat Sorgulama")
public class AracDegerlemeTest extends BaseTest {

    @Description("Araç değerleme ve ilan kontrolü testi")
    @Severity(SeverityLevel.CRITICAL)
    @RepeatedTest(1)
    @Execution(ExecutionMode.CONCURRENT)
    public void testAracDegerlemeAndIlanlar() throws InterruptedException {

        logInfo("Test başlatılıyor: Araç Değerleme ve İlan Kontrolü");

        // 1. Ana sayfaya git ve çerezleri kabul et
        HomePage homePage = new HomePage(driver);
        homePage.navigateToHomePage(config.getBaseUrl());
        homePage.acceptCookiesIfPresent();
        logInfo("Ana sayfaya gidildi");


        // 2. Araç Değerleme sayfasına git
        AracDegerlemePage aracDegerlemePage = homePage.navigateToAracDegerleme();
        logInfo("Araç Değerleme sayfasına yönlendirildi");

        // 3. URL kontrolü either ""oto360/arac-degerleme/alirken" or "oto360/arac-degerleme/satarken"
        aracDegerlemePage.verifyUrl("oto360/arac-degerleme/alirken", "oto360/arac-degerleme/satarken");
        logInfo("URL doğrulandı");

        // 4. Araç detayları seçimi
        aracDegerlemePage.selectBinek();
        aracDegerlemePage.selectCategory("2020");
        aracDegerlemePage.selectCategory("Citroen");
        aracDegerlemePage.selectCategory("C3");
        aracDegerlemePage.selectCategory("Benzin");
        aracDegerlemePage.selectCategory("Hatchback 5 kapı");
        aracDegerlemePage.selectCategory("1.2 PureTech");
        aracDegerlemePage.selectCategory("Live");
        aracDegerlemePage.selectCategory("C3 1.2 PureTech Live");
        aracDegerlemePage.selectKilometre("40 000");
        logInfo("Tüm araç detayları seçildi");
        aracDegerlemePage.seeResults();
        logInfo("Sonuçları gör butonuna tıklandı");

        // 6. Fiyat aralıklarının kontrolü
        aracDegerlemePage.verifyValueRanges();
        logInfo("Fiyat aralıkları doğrulandı");

        // 7. Aynı marka ve modelin ilanlarını listele
        IlanlarPage ilanlarPage = aracDegerlemePage.clickIlanlariListele();
        logInfo("İlanları listele butonuna tıklandı");

        // 8. İlan yoksa filtrelerden birini kaldır
        if (!ilanlarPage.areIlansDisplayed()) {
            logInfo("İlan bulunamadı, filtre kaldırılıyor");
            ilanlarPage.removeFiltersUntilIlansDisplayed();
        }

        // 9. İlanların listelendiği kontrol edilir
        assertTrue(ilanlarPage.areIlansDisplayed(), "İlanlar listelenmelidir");
        logInfo("İlanlar listelendi");

        // 10. Fiyata göre önce en düşük filtresi uygulanır
        ilanlarPage.sortByLowestPrice();
        logInfo("İlanlar fiyata göre sıralandı (önce en düşük)");

        // 11. Filtrenin doğru çalıştığı kontrol edilir
        assertTrue(ilanlarPage.arePricesInAscendingOrder(), "İlanlar fiyata göre artan şekilde sıralanmalıdır");
        logInfo("Fiyat sıralaması doğrulandı");

        // 12. İlk ilan detaylarını kaydet
        String firstIlanTitle = ilanlarPage.getFirstIlanTitle();
        double firstIlanPrice = ilanlarPage.getFirstIlanPrice();
        logInfo("İlk ilan bilgileri: " + firstIlanTitle + ", Fiyat: " + firstIlanPrice);

        // 13. İlk ilana tıkla
        IlanDetayPage ilanDetayPage = ilanlarPage.clickFirstIlan();
        logInfo("İlk ilana tıklandı");

        // 14. İlan detay sayfasında olduğunu doğrula
        assertTrue(ilanDetayPage.isPageLoaded(), "İlan detay sayfası yüklenmedi");
        logInfo("İlan detay sayfası yüklendi");

        // 15. İlan başlığının ve fiyatının listedekiyle aynı olduğunu doğrula
        assertEquals(firstIlanTitle, ilanDetayPage.getIlanTitle(), "İlan başlıkları eşleşmelidir");
        assertEquals(firstIlanPrice, ilanDetayPage.getIlanPrice(), "İlan fiyatları eşleşmelidir");
        logInfo("İlan detay bilgileri doğrulandı");
    }
}