import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdUnitTest {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(700, TimeUnit.SECONDS);
    }

    @Test
    public void testAdUnit() throws IOException, org.json.simple.parser.ParseException {
        driver.get("https://www.timesnownews.com/");
        List<WebElement> listAdUnit = waitForElementsToBePresent(By.cssSelector("div[data-adunit]"), Duration.ofSeconds(700));
        int countSize = listAdUnit.size();
        System.out.println("Total Data-Ad: " + countSize);

       
        List<String> webPageAdUnits = new ArrayList<>();
        for (int i = 0; i < countSize; i++) {
            String divAd = listAdUnit.get(i).getAttribute("data-adunit");
            if (divAd != null && !divAd.isEmpty()) {
                System.out.println("Ad Unit: " + (i + 1) + " : " + divAd);
                webPageAdUnits.add(divAd);
            }
        }

        System.out.println("--------------------------------------------------------------------------------------------");
        JSONTokener jsonTokener = new JSONTokener(new FileReader("./src/test/resources/TnAd.json"));
        JSONObject jsonObject = new JSONObject(jsonTokener);
        JSONObject ads = jsonObject.getJSONObject("response").getJSONObject("ads");

        
        List<String> adCodesFromJSON = new ArrayList<>();
        for (String adUnitKey : ads.keySet()) {
            JSONObject adUnit = ads.getJSONObject(adUnitKey);
            String adCode = adUnit.getString("adCode");
            adCodesFromJSON.add(adCode);
        }

       
        List<String> unmatchedAdUnits = new ArrayList<>();
        for (String adUnit : webPageAdUnits) {
            boolean matched = false;
            for (String adCode : adCodesFromJSON) {
                if (adCode.contains(adUnit)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                unmatchedAdUnits.add(adUnit);
            }
        }

       
        System.out.println("Unmatched Ad Units:");
        for (String unmatchedAdUnit : unmatchedAdUnits) {
            System.out.println(unmatchedAdUnit);
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    private List<WebElement> waitForElementsToBePresent(By locator, Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }
}
