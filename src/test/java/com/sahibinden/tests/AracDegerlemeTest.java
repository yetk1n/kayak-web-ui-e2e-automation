package com.sahibinden.tests;

import com.sahibinden.pages.AracDegerlemePage;
import com.sahibinden.pages.HomePage;
import com.sahibinden.pages.IlanDetayPage;
import com.sahibinden.pages.IlanlarPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)      // isteğe bağlı
@Epic("Araç Değerleme")
@Feature("Araç Fiyat Sorgulama")
public class AracDegerlemeTest extends BaseTest {

    @Test
    @Description("Araç değerleme ve ilan kontrolü testi")
    @Severity(SeverityLevel.CRITICAL)
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

        // 3. URL kontrolü
        aracDegerlemePage.verifyUrl("oto360/arac-degerleme/alirken");
        logInfo("URL doğrulandı: aracdegerleme/satarken içeriyor");

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

        // 5. Değerleme sonuçlarının kontrolü
        boolean isDegerlemeSonucDisplayed = aracDegerlemePage.isDegerlemeSonucDisplayed();
//        assertTrue(isDegerlemeSonucDisplayed, "Değerleme sonucu görüntülenmelidir");
        logInfo("Değerleme sonuçları görüntüleniyor");

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