import DataFetcher.DataFetcher;
import ProductManager.Entities.Product;
import ProductManager.ProductManager;

import java.time.Instant;


public class Main {

    public static void main(String[] args) {

        DataFetcher dataFetcher = new DataFetcher();
        dataFetcher.fetch();

//        ProductManager pm = ProductManager.load();
//
//
//        var productOptional = pm.findByBarcode(3850334009944L);
//
//        if(productOptional.isPresent()){
//            Product prod = productOptional.get();
//            System.out.println(prod.getName());
//            System.out.println(prod.getBrand());
//            System.out.println(prod.getPrices().getFirst().getPrice());
//        }else {
//            System.out.println("Nothing found.");
//        }
//
//        long start = System.nanoTime();
//        var cocaCola = pm.findByProductName("coca");
//        long end = System.nanoTime();
//        cocaCola.forEach( dto -> {
//            System.out.println(dto.getProductName() + " : " + dto.getBarcode());
//        });
//
//        long nanoTime = end - start;
//        System.out.println("Fetch time = " + nanoTime);
    }

}


