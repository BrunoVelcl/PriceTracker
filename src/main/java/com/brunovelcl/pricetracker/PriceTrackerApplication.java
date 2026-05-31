package com.brunovelcl.pricetracker;

import com.brunovelcl.pricetracker.DataFetcher.DataFetcher;
import com.brunovelcl.pricetracker.DataFetcher.Unzipper;
import com.brunovelcl.pricetracker.DataFetcher.entities.Chain;
import com.brunovelcl.pricetracker.DataParser.entities.ParsedValuesContainer;
import com.brunovelcl.pricetracker.DataParser.parsers.Parser;
import com.brunovelcl.pricetracker.ProductManager.SaveFIleManager.SaveFileManager;
import com.brunovelcl.pricetracker.Text.Text;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class PriceTrackerApplication {

	public static void main(String[] args) {

		//Unzipper.unzipAllInDir(Text.Directories.TEMP + "SPAR/");

		SpringApplication.run(PriceTrackerApplication.class, args);
	}

}


