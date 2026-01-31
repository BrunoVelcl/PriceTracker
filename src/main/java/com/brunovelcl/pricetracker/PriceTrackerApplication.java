package com.brunovelcl.pricetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
public class PriceTrackerApplication {

	public static void main(String[] args) {

		//Trust store needed for Chain.Plodine
		System.setProperty("javax.net.ssl.trustStore", Paths.get("certs/truststore.jks").toAbsolutePath().toString());
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit"); //TODO: change and add password to env when deploying

		SpringApplication.run(PriceTrackerApplication.class, args);
	}

}
