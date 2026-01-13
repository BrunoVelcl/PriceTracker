package ProductManager.SaveFIleManager;

import DataParser.entities.Store;
import DataParser.repositories.StoreRepoImpl;
import ProductManager.Entities.Builders.ProductBuilder;
import ProductManager.Entities.PricePoint;
import ProductManager.Entities.Product;
import ProductManager.ProductManager;
import Text.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SaveFileManager {

    private static final Path SAVEFILE = Paths.get("data", "save");
    private static final String LEVEL_1_DELIMITER = ";";
    private static final String LEVEL_2_DELIMITER = "@";
    private static final String LEVEL_3_DELIMITER = "~";

    public static void save(ConcurrentHashMap<Long, Product> productHashMap, StringBuilder sb){
        try(BufferedWriter bw = Files.newBufferedWriter(SAVEFILE, StandardOpenOption.TRUNCATE_EXISTING)) {
            productHashMap.forEach((key, value) -> {
                sb.setLength(0);
                sb.append(value.getBarcode()).append(LEVEL_1_DELIMITER)
                        .append(value.getName()).append(LEVEL_1_DELIMITER)
                        .append(value.getBrand()).append(LEVEL_1_DELIMITER)
                        .append(value.getUnit_quantity()).append(LEVEL_1_DELIMITER)
                        .append(value.getUnit()).append(LEVEL_1_DELIMITER);

                List<PricePoint> prices = value.getPrices();
                for(PricePoint pp : prices){
                   sb.append(LEVEL_2_DELIMITER).append(pp.getPrice());
                   List<Store> stores = pp.getStores();
                   for(Store store: stores){
                       sb.append(LEVEL_3_DELIMITER).append(store.getId());
                   }
                }
                try {
                    bw.write(sb.toString());
                    bw.newLine();
                } catch (IOException e) {
                    System.err.printf(Text.ErrorMessagess.IO_EXCEPTION_BUFF_WRITER, sb);
                }
            });

        }catch (Exception e){
            System.err.println(Text.ErrorMessagess.SAVE_FILE_WRITE_ERROR);
        }
    }

    public static ConcurrentHashMap<Long, Product> load(){
        ConcurrentHashMap<Long, Product> productsMap = new ConcurrentHashMap<>();
        try(BufferedReader br = Files.newBufferedReader(SAVEFILE)) {
            StoreRepoImpl stores = StoreRepoImpl.load();
            String line = br.readLine();
            ProductBuilder pb = new ProductBuilder();
            while (line != null){
                String[] levelOneSeparation =  line.split(LEVEL_1_DELIMITER);
                pb.barcode(Long.parseLong(levelOneSeparation[0]));
                pb.name(levelOneSeparation[1]);
                pb.brand(levelOneSeparation[2]);
                pb.unitQuantity(levelOneSeparation[3]);
                pb.unit(levelOneSeparation[4]);

                String[] levelTwoSeparation = levelOneSeparation[5].split(LEVEL_2_DELIMITER);
                for(int i = 1; i < levelTwoSeparation.length; i++){
                    String[] levelThreeSeparation = levelTwoSeparation[i].split(LEVEL_3_DELIMITER);
                    Double pricePointValue = Double.parseDouble(levelThreeSeparation[0]);
                    PricePoint pricePoint = new PricePoint(pricePointValue);
                    for (int j = 1; j < levelThreeSeparation.length; j++) {
                        pricePoint.getStores().add(stores.getStores().get(Short.parseShort(levelThreeSeparation[j])));
                    }
                    pb.addPricePoint(pricePoint);
                }

                productsMap.put(pb.getBarcode(), pb.consume());

                line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println(Text.ErrorMessagess.SAVE_FILE_READ_ERROR);
        }
        return productsMap;
    }
}
