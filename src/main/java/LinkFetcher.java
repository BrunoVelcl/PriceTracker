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

    public void getLinksKaufland(){
        String rootUrl = "https://www.kaufland.hr";
        String pricePageUrl = "https://www.kaufland.hr/akcije-novosti/popis-mpc.html";

        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        WebDriver driver = new FirefoxDriver(options);
        driver.get(pricePageUrl);
        WebElement cookieButton = driver.findElement(By.id("onetrust-accept-btn-handler"));
        cookieButton.click();

        WebDriverWait driverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(".csv")));
        System.out.printf("Waiting on: %s\r", rootUrl);

        List<WebElement> links = driver.findElements(By.partialLinkText(".csv"));

        String filePath = "G:\\Dev\\Prices\\links\\kaufland.csv";

        File file = new File(filePath);

        StringBuilder sb = new StringBuilder();

        try (FileWriter writer = new FileWriter(file)){
            for(WebElement link : links){

                sb.append(link.getAccessibleName()).deleteCharAt(0)
                    .append(",")
                    .append(rootUrl)
                    .append(link.getDomAttribute("href"))
                    .append("\n");

                //Cleans the URL if there are white spaces.
                for(int i = Objects.requireNonNull(link.getAccessibleName()).length(); i < sb.length(); i++){
                    if (sb.charAt(i) == ' '){
                        sb.deleteCharAt(i);
                        sb.insert(i, "%20");
                        i+=2;
                    }
                }

                writer.write(sb.toString());
                sb.setLength(0);
            }
        }catch (IOException e){
            System.err.println("Can't write to: " + filePath);
        }

        driver.quit();

        System.out.println("\u001b[32mUPDATED\u001b[37m: " + rootUrl);

    }
}
