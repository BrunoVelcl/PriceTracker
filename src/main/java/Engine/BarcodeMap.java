package Engine;

import Parser.ParsedValues;
import Parser.ProductInfo;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BarcodeMap implements Serializable{
    Map<Long, PricePoint> barcodeMap = new HashMap<>();

    public void update(ParsedValues pv){
        if(barcodeMap.containsKey(pv.getBarcode())){
            PricePoint firstNode = barcodeMap.get(pv.getBarcode());
            firstNode.updatePrice(firstNode, pv);
        }else {
            barcodeMap.put(
                    pv.getBarcode(),
                    new PricePoint(
                            new ProductInfo(
                                    pv.getBarcode(), pv.getProductName(), pv.getBrand(), pv.getUnit_quantity(), pv.getUnit()
                            ),
                            pv.getPrice()
                    )
            );
            PricePoint firstNode = barcodeMap.get(pv.getBarcode());
            firstNode.addStore(pv.getStoreInfo());
        }
    }

    public List<PricePoint> getPricesForBarcode(Long barcode){
        if(!barcodeMap.containsKey(barcode)){
            return null;
        }
        PricePoint Node = barcodeMap.get(barcode);
        List<PricePoint> list = new ArrayList<>();
        while(Node != null){
            list.add(Node);
            Node = Node.getNextNode();
        }
        Collections.sort(list);
        return list;
    }

}


