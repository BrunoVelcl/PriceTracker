import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

//**Caller owns the driver, must call .free when done with the object instance*/
public class LinkFetcher {

    private List<StoreNameLinks> linksBin;
    private final String linksBinDir = "links.bin";
    private final WebDriver driver;
    private final WebDriverWait driverWait;
    private final String updatedGreen = "\u001b[32mUPDATED\u001b[37m: ";
    private final StringBuilder sb = new StringBuilder();

    @SuppressWarnings("unchecked") // for the (List<StoreNameLinks>) cast
    public LinkFetcher() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        driver = new FirefoxDriver(options);
        driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));

       try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(linksBinDir))){
           linksBin = (List<StoreNameLinks>) inputStream.readObject();
       }catch(IOException | ClassNotFoundException e){
            linksBin = new ArrayList<>();
       }

    }

    public void writeLinksToDisk(){
        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(linksBinDir))){
            outputStream.writeObject(linksBin);
        }catch(IOException e){
            System.err.println("FAILED TO WRITE LINK DATA TO DISK");
        }
    }


    public void free(){
        driver.quit();
    }

    public List<StoreNameLinks> getLinksBin(){
        return linksBin;
    }

    public void getLinksKaufland() {
        Store store = Store.KAUFLAND;
        String rootUrl = "https://www.kaufland.hr";
        String pricePageUrl = "https://www.kaufland.hr/akcije-novosti/popis-mpc.html";

        driver.get(pricePageUrl);
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(".csv")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.partialLinkText(".csv"));

        for (WebElement link : links) {
            sb.setLength(0);
            sb.append(rootUrl).append(link.getDomAttribute("href"));
            cleanURL(sb);

            StoreNameLinks storeLink = new StoreNameLinks(
                    Objects.requireNonNull(link.getAccessibleName()).substring(1),
                    sb.toString(),
                    store
            );
            if(!storeLink.inList(linksBin)) {
                linksBin.add(storeLink);
            }
        }

        System.out.println(updatedGreen + rootUrl);

    }

    public void getLinksSpar(){
        Store store = Store.SPAR;
        String rootUrl = "https://www.spar.hr/";
        String pricePageUrl = "https://www.spar.hr/usluge/cjenici";

        driver.get(pricePageUrl);
        driver.switchTo().frame(4);
        try {
            driverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Preuzmi")));
        }catch (Exception e){
            System.out.println("SPAR page timeout");
            return;
        }
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.linkText("Preuzmi"));

        for (WebElement link : links) {
            sb.setLength(0);
            sb.append(link.getDomAttribute("href"));
            sb.delete(0,sb.lastIndexOf("/")+1);
            cleanURL(sb);

            StoreNameLinks storeLink = new StoreNameLinks(
                    sb.toString(),
                    link.getDomAttribute("href"),
                    store
            );

            if(!storeLink.inList(linksBin)) {
                linksBin.add(storeLink);
            }
        }

        System.out.println(updatedGreen + rootUrl);

    }
    
    public void getLinksLidl() {
        Store store = Store.LIDL;
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
        
        String linkStr = links.getLast().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        if (sb.isEmpty()) {
            System.err.println("Failed to get link for: " + rootUrl);
            return;
        }
        sb.delete(0,sb.lastIndexOf("/")+1);

        StoreNameLinks storeLink = new StoreNameLinks(sb.toString(),linkStr,store);
        if(!storeLink.inList(linksBin)) {
            linksBin.add(storeLink);
        }

        System.out.println(updatedGreen + rootUrl);
    }

    public void getLinksPlodine() {
        Store store = Store.PLODINE;
        String rootUrl = "https://www.plodine.hr/";
        String pricePageUrl = "https://www.plodine.hr/info-o-cijenama";

        driver.get(pricePageUrl);
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Prihvaćam']")));
        WebElement cookieButton = driver.findElement(By.xpath("//button[text()='Prihvaćam']"));
        cookieButton.click();

        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.cssSelector("a[href$='.zip']"));
        
        String linkStr = links.getFirst().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        if (sb.isEmpty()) {
            System.err.println("Failed to get link for: " + rootUrl);
            return;
        }
        sb.delete(0,sb.lastIndexOf("/")+1);

        StoreNameLinks storeLink = new StoreNameLinks(sb.toString(),linkStr,store);
        if(!storeLink.inList(linksBin)) {
            linksBin.add(storeLink);
        }
        
        System.out.println(updatedGreen + rootUrl);
    }

    public void getLinksStudenac(){
        Store store = Store.STUDENAC;
        String rootUrl = "https://www.studenac.hr/";
        String pricePageUrl = "https://www.studenac.hr/popis-maloprodajnih-cijena";

        driver.get(pricePageUrl);
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='.zip']")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.cssSelector("a[href$='.zip']"));
        
        String linkStr = links.getFirst().getDomAttribute("href");
        sb.setLength(0);
        sb.append(linkStr);
        if (sb.isEmpty()) {
            System.err.println("Failed to get link for: " + rootUrl);
            return;
        }
        sb.delete(0,sb.lastIndexOf("/")+1);

        StoreNameLinks storeLink = new StoreNameLinks(sb.toString(),linkStr,store);
        if(!storeLink.inList(linksBin)) {
            linksBin.add(storeLink);
        }
        
        System.out.println(updatedGreen + rootUrl);
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
