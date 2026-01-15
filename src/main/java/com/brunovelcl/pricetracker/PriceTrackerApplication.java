package com.brunovelcl.pricetracker;

import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.parsers.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PriceTrackerApplication {

	public static void main(String[] args) {

		//var _ = Parser.run(Chain.LIDL);

		SpringApplication.run(PriceTrackerApplication.class, args);
	}

}
