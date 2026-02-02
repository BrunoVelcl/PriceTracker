package com.brunovelcl.pricetracker;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.parsers.Parser;
import com.brunovelcl.pricetracker.ProductManager.SaveFIleManager.SaveFileManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
public class PriceTrackerApplication {

	public static void main(String[] args) {

//		ParsedValuesContainer parsedValuesContainer = Parser.run(Chain.LIDL);
//		SaveFileManager.saveParsedValues(parsedValuesContainer, Chain.LIDL);

		//Trust store needed for some chains
		System.setProperty("javax.net.ssl.trustStore", Paths.get("certs/truststore.jks").toAbsolutePath().toString());
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit"); //TODO: change and add password to env when deploying

		SpringApplication.run(PriceTrackerApplication.class, args);
	}

}
