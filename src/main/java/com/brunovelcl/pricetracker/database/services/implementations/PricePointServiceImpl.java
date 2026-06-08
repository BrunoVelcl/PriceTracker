package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointDTO;
import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointRowDTO;
import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointView;
import com.brunovelcl.pricetracker.database.entities.BrandNameProduct;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.repositories.PricePointRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PricePointServiceImpl implements PricePointService {

    private final PricePointRepository pricePointRepository;

    public PricePointServiceImpl(PricePointRepository pricePointRepository) {
        this.pricePointRepository = pricePointRepository;
    }

    @Override
    public PricePoint save(PricePoint pricePoint) {
        return this.pricePointRepository.save(pricePoint);
    }

    @Override
    public Optional<PricePoint> findByPriceAndBrandNameProduct(BigDecimal price, BrandNameProduct product) {
        return this.pricePointRepository.findByPriceAndBrandNameProduct(price, product);
    }

    @Override
    public List<PricePoint> findByBrandNameProduct(BrandNameProduct brandNameProduct) {
        return pricePointRepository.findByBrandNameProduct(brandNameProduct);
    }

    @Override
    public List<PricePointDTO> findAllCustom() {
        List<PricePointView> view = this.pricePointRepository.findAllCustom();
        List<PricePointDTO> dtolist = new ArrayList<>();

        view.forEach(row -> {
            dtolist.add(new PricePointDTO(row.getId(), row.getPriceEuros(), row.getBrandNameProductId()));
        });

        return dtolist;
    }

    @Override
    public void ingestTable(List<PricePointRowDTO> list, String tableName) throws Exception {
        this.pricePointRepository.ingestTable(list, tableName);
    }
}
