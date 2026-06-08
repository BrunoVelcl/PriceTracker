package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreDTO;
import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreView;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import com.brunovelcl.pricetracker.database.repositories.PricePointStoreRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointStoreService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class PricePointStoreServiceImpl implements PricePointStoreService {

    PricePointStoreRepository ppsRepo;

    public PricePointStoreServiceImpl(PricePointStoreRepository ppsRepo) {
        this.ppsRepo = ppsRepo;
    }

    public PricePointStore save(PricePointStore pricePointStore) {
        return this.ppsRepo.save(pricePointStore);
    }

    @Override
    public void delete(PricePointStore pricePointStore) {
        this.ppsRepo.delete(pricePointStore);
    }

    @Override
    public List<PricePointStore> findAll() {
        return this.ppsRepo.findAll();
    }

    @Override
    public List<PricePointStoreDTO> findAllCustom() {
        List<PricePointStoreView> view = this.ppsRepo.findAllRows();
        List<PricePointStoreDTO> dtoList = new ArrayList<>();

        view.forEach(row -> {
            dtoList.add(new PricePointStoreDTO(row.getPricePointId(), row.getStoreId(), row.getLastUpdated()));
        });

        return dtoList;
    }

    @Override
    public void ingestTable(List<PricePointStoreDTO> list, String tableName) throws Exception {
        this.ppsRepo.ingestTable(list, tableName);
    }
}
