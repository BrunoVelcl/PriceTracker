package DataFetcher;

import ANSIEscapes.ANSI;
import DataFetcher.entities.ChainWebInfo;
import DataFetcher.entities.DownloadLink;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkScraper {

    private final WebDriver driver;
    private final WebDriverWait driverWait;
    private final StringBuilder sb;
    private final  List<DownloadLink> scrapedLinks;


    public LinkScraper(StringBuilder stringBuilder) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        this.driver = new FirefoxDriver(options);
        this.driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.sb = stringBuilder;
        this.scrapedLinks = new ArrayList<>();
    }

    public List<DownloadLink> getLinks(ChainWebInfo chainWebInfo)throws IllegalArgumentException{
        System.out.printf(ANSI.Messages.WAITING_FOR_WEBPAGE, chainWebInfo.getBaseUrl());
        switch (chainWebInfo.getChain()){
            case LIDL -> {
                return getLinksLidl(chainWebInfo);
            }
            case KAUFLAND -> {
                return getLinksKaufland(chainWebInfo);
            }
            case SPAR -> {
                return getLinksSpar(chainWebInfo);
            }
            case STUDENAC -> {
                return getLinksStudenac(chainWebInfo);
            }
            case PLODINE -> {
                return getLinksPlodine(chainWebInfo);
            }
            default -> {
                throw new IllegalArgumentException("Unsuported store encountered.");
            }
        }
    }

    private List<DownloadLink> getLinksKaufland(ChainWebInfo chainWebInfo) {

        this.driver.get(chainWebInfo.getPriceDataUrl());
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(".csv")));

        List<WebElement> links = driver.findElements(By.partialLinkText(".csv"));

        for (WebElement link : links) {
            this.sb.setLength(0);
            this.sb.append(chainWebInfo.getBaseUrl()).append(link.getDomAttribute("href"));
            cleanURL(this.sb);

            this.scrapedLinks.add(new DownloadLink(
                    Objects.requireNonNull(link.getAccessibleName()).substring(1),
                    this.sb.toString()));
        }
        System.out.printf(ANSI.Messages.FINISHED_SCRAPING, chainWebInfo.getBaseUrl());
        return this.scrapedLinks;
    }

    private List<DownloadLink> getLinksSpar(ChainWebInfo chainWebInfo){

        this.driver.get(chainWebInfo.getPriceDataUrl());
        driver.switchTo().frame(4);

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Preuzmi")));

        List<WebElement> links = driver.findElements(By.linkText("Preuzmi"));

        for (WebElement link : links) {
            sb.setLength(0);
            sb.append(link.getDomAttribute("href"));
            sb.delete(0,sb.lastIndexOf("/")+1);
            cleanURL(sb);

            this.scrapedLinks.add(new DownloadLink(
                    sb.toString(),
                    link.getDomAttribute("href")));
        }
        System.out.printf(ANSI.Messages.FINISHED_SCRAPING, chainWebInfo.getBaseUrl());
        return this.scrapedLinks;
    }

    public List<DownloadLink> getLinksLidl(ChainWebInfo chainWebInfo) {

        driver.get(chainWebInfo.getPriceDataUrl());
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("onetrust-accept-btn-handler")));
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("ovdje")));

        List<WebElement> links = driver.findElements(By.partialLinkText("ovdje"));
        links.removeLast(); //there is something else on the site with the same link text

        String linkStr = links.getLast().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        sb.delete(0,sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new DownloadLink(sb.toString(),linkStr));
        System.out.printf(ANSI.Messages.FINISHED_SCRAPING, chainWebInfo.getBaseUrl());
        return this.scrapedLinks;
    }

    public List<DownloadLink> getLinksPlodine(ChainWebInfo chainWebInfo) {

        driver.get(chainWebInfo.getPriceDataUrl());
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Prihvaćam']")));
        WebElement cookieButton = driver.findElement(By.xpath("//button[text()='Prihvaćam']"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));

        List<WebElement> links = driver.findElements(By.cssSelector("a[href$='.zip']"));

        String linkStr = links.getFirst().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        sb.delete(0,sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new DownloadLink(sb.toString(),linkStr));
        System.out.printf(ANSI.Messages.FINISHED_SCRAPING, chainWebInfo.getBaseUrl());
        return this.scrapedLinks;
    }

    public List<DownloadLink> getLinksStudenac(ChainWebInfo chainWebInfo){

        driver.get(chainWebInfo.getPriceDataUrl());
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));

        List<WebElement> links = driver.findElements(By.cssSelector("a[href$='.zip']"));

        String linkStr = links.getFirst().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        sb.delete(0,sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new DownloadLink(sb.toString(),linkStr));
        System.out.printf(ANSI.Messages.FINISHED_SCRAPING, chainWebInfo.getBaseUrl());
        return this.scrapedLinks;
    }

    private void cleanURL(StringBuilder url) {
        //Cleans the URL if there are white spaces.
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == ' ') {
                url.deleteCharAt(i);
                url.insert(i, "%20");
                i += 2;
            }
        }
    }
}
