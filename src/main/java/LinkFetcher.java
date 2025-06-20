import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class LinkFetcher {

    private final WebDriver driver;
    private final WebDriverWait driverWait;
    private final String updatedGreen = "\u001b[32mUPDATED\u001b[37m: ";

    public LinkFetcher() {
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("--headless");
        driver = new FirefoxDriver(options);
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void getLinksKaufland() {
        String rootUrl = "https://www.kaufland.hr";
        String pricePageUrl = "https://www.kaufland.hr/akcije-novosti/popis-mpc.html";

        driver.get(pricePageUrl);
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(".csv")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.partialLinkText(".csv"));

        String filePath = "G:\\Dev\\Prices\\links\\kaufland.csv";

        File file = new File(filePath);

        StringBuilder sb = new StringBuilder();

        try (FileWriter writer = new FileWriter(file)) {
            for (WebElement link : links) {

                sb.append(link.getAccessibleName()).deleteCharAt(0)
                        .append(",")
                        .append(rootUrl)
                        .append(link.getDomAttribute("href"))
                        .append("\n");

                //Cleans the URL if there are white spaces.
                for (int i = Objects.requireNonNull(link.getAccessibleName()).length(); i < sb.length(); i++) {
                    if (sb.charAt(i) == ' ') {
                        sb.deleteCharAt(i);
                        sb.insert(i, "%20");
                        i += 2;
                    }
                }

                writer.write(sb.toString());
                sb.setLength(0);
            }
        } catch (IOException e) {
            System.err.println("Can't write to: " + filePath);
        }

        driver.quit();

        System.out.println(updatedGreen + rootUrl);

    }

    public void getLinksLidl() {

        String rootUrl = "https://www.lidl.hr/";
        String pricePageUrl = "https://tvrtka.lidl.hr/cijene";

        driver.get(pricePageUrl);
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("onetrust-accept-btn-handler")));
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("ovdje")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.partialLinkText("ovdje"));
        links.removeLast(); //there is something else on the site with the same link text

        String filePath = "G:\\Dev\\Prices\\links\\lidl.csv";

        File file = new File(filePath);
        String link = links.getLast().getDomAttribute("href");
        if (link == null) {
            System.err.println("Failed to get link for: " + rootUrl);
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Lidl.zip," + link);
        } catch (IOException e) {
            System.err.println("Can't write to: " + filePath);
        }

        System.out.println(updatedGreen + rootUrl);
        driver.quit();
    }

    public void getLinksPlodine() {
        String rootUrl = "https://www.plodine.hr/";
        String pricePageUrl = "https://www.plodine.hr/info-o-cijenama";

        driver.get(pricePageUrl);
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Prihvaćam']")));
        WebElement cookieButton = driver.findElement(By.xpath("//button[text()='Prihvaćam']"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.cssSelector("a[href$='.zip']"));

        String filePath = "G:\\Dev\\Prices\\links\\plodine.csv";
        String link = links.getFirst().getDomAttribute("href");
        if (link == null) {
            System.err.println("Failed to get link for: " + rootUrl);
            return;
        }

        File file = new File(filePath);
        try(FileWriter writer = new FileWriter(file)){
            writer.write("Plodine.zip," + link);
        }catch(IOException e){
            System.err.println("Can't write to: " + filePath);
        }

        System.out.println(updatedGreen + rootUrl);
        driver.quit();
    }
}
