package com.brunovelcl.pricetracker.DataFetcher;

import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import com.brunovelcl.pricetracker.schedulers.entities.ChainInfo;
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
    private final List<ScrapedLink> scrapedLinks;


    public LinkScraper(StringBuilder stringBuilder) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        this.driver = new FirefoxDriver(options);
        this.driverWait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        this.sb = stringBuilder;
        this.scrapedLinks = new ArrayList<>();
    }

    public List<ScrapedLink> getLinks(ChainInfo chainInfo)throws IllegalArgumentException{
        System.out.printf(Text.Messages.WAITING_FOR_WEBPAGE, chainInfo.getChain().getName());

        switch (chainInfo.getChain().getName()){
            case "LIDL" -> {
                return getLinksLidl(chainInfo);
            }
            case "KAUFLAND" -> {
                return getLinksKaufland(chainInfo);
            }
            case "SPAR" -> {
                return getLinksSpar(chainInfo);
            }
            case "STUDENAC" -> {
                return getLinksStudenac(chainInfo);
            }
            case "PLODINE" -> {
                return getLinksPlodine(chainInfo);
            }
            default -> {
                throw new IllegalArgumentException("Unsuported store encountered.");
            }
        }
    }

    private List<ScrapedLink> getLinksKaufland(ChainInfo chainInfo) {
        List<WebElement> links = null;
        try {
            this.driver.get(chainInfo.getChain().getPriceCatalogWebAddress());
            WebElement cookieButton = this.driver.findElement(By.id("onetrust-accept-btn-handler"));
            cookieButton.click();

            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(".csv")));

            links = this.driver.findElements(By.partialLinkText(".csv"));
        } catch (Exception e) {
            this.driver.quit();
            return null;
        }

        for (WebElement link : links) {
            this.sb.setLength(0);
            this.sb.append(chainInfo.getChain().getWebAddress()).append(link.getDomAttribute("href"));
            cleanURL(this.sb);

            this.scrapedLinks.add(new ScrapedLink(
                    this.sb.toString(),
                    Objects.requireNonNull(link.getAccessibleName()).substring(1),
                    chainInfo.getChain()
                    ));
        }
        this.driver.quit();
        return this.scrapedLinks;
    }

    private List<ScrapedLink> getLinksSpar(ChainInfo chainInfo){
        List<WebElement> links = null;
        try {
            this.driver.get(chainInfo.getChain().getPriceCatalogWebAddress());
            this.driver.switchTo().frame(4);

            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Preuzmi")));

            links = this.driver.findElements(By.linkText("Preuzmi"));
        } catch (Exception e) {
            this.driver.quit();
            return null;
        }

        for (WebElement link : links) {
            this.sb.setLength(0);
            this.sb.append(link.getDomAttribute("href"));
            this.sb.delete(0,this.sb.lastIndexOf("/")+1);
            cleanURL(this.sb);

            this.scrapedLinks.add(new ScrapedLink(
                    link.getDomAttribute("href"),
                    this.sb.toString(),
                    chainInfo.getChain()));
        }
        this.driver.quit();
        return this.scrapedLinks;
    }

    public List<ScrapedLink> getLinksLidl(ChainInfo chainInfo) {
        List<WebElement> links = null;
        try {
            this.driver.get(chainInfo.getChain().getPriceCatalogWebAddress());
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("onetrust-accept-btn-handler")));
            WebElement cookieButton = this.driver.findElement(By.id("onetrust-accept-btn-handler"));
            cookieButton.click();

            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("ovdje")));
            links = this.driver.findElements(By.partialLinkText("ovdje"));
        } catch (Exception e) {
            this.driver.quit();
            return null;
        }

        links.removeLast(); //there is something else on the site with the same link text

        String linkStr = links.getLast().getDomAttribute("href");
        this.sb.setLength(0);
        this.sb.append(linkStr);
        this.sb.delete(0, this.sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new ScrapedLink(linkStr, this.sb.toString(), chainInfo.getChain()));

        this.driver.quit();
        return this.scrapedLinks;
    }

    public List<ScrapedLink> getLinksPlodine(ChainInfo chainInfo) {

        List<WebElement> links = null;
        try {
            this.driver.get(chainInfo.getChain().getPriceCatalogWebAddress());
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Prihvaćam']")));
            WebElement cookieButton = this.driver.findElement(By.xpath("//button[text()='Prihvaćam']"));
            cookieButton.click();

            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));

            links = this.driver.findElements(By.cssSelector("a[href$='.zip']"));
        } catch (Exception e) {
            this.driver.quit();
            return null;
        }

        String linkStr = links.getFirst().getDomAttribute("href");
        this.sb.setLength(0);
        this.sb.append(linkStr);
        this.sb.delete(0, this.sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new ScrapedLink(linkStr, this.sb.toString(), chainInfo.getChain()));

        this.driver.quit();
        return this.scrapedLinks;
    }

    public List<ScrapedLink> getLinksStudenac(ChainInfo chainInfo){
        List<WebElement> links = null;
        try {
            this.driver.get(chainInfo.getChain().getPriceCatalogWebAddress());
            this.driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));

            links = this.driver.findElements(By.cssSelector("a[href$='.zip']"));
        }catch (Exception e){
            this.driver.quit();
            return null;
        }

        String linkStr = links.getFirst().getDomAttribute("href");
        this.sb.setLength(0);
        this.sb.append(linkStr);
        this.sb.delete(0, this.sb.lastIndexOf("/")+1);

        this.scrapedLinks.add(new ScrapedLink(linkStr, this.sb.toString(), chainInfo.getChain()));

        this.driver.quit();
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
