package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTO;
import com.brunovelcl.pricetracker.database.DTOs.PricePointPricePointStoreDTOView;
import com.brunovelcl.pricetracker.database.entities.PricePoint;
import com.brunovelcl.pricetracker.database.entities.PricePointStore;
import com.brunovelcl.pricetracker.database.repositories.PricePointStoreRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.PricePointStoreService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Optional<PricePointPricePointStoreDTO> findByBrandNameProductAndStore(Long productId, Integer storeId) {
        List<PricePointPricePointStoreDTOView> viewList = this.ppsRepo.findByBrandNameProductAndStore(productId, storeId);
        if(viewList.isEmpty()) return Optional.empty();
        PricePointPricePointStoreDTOView view = viewList.getFirst();
        return Optional.of(
                new PricePointPricePointStoreDTO(
                        view.getPricePointId(),
                        view.getPrice(),
                        view.getBrandNameProductId(),
                        view.getPricePointStoreId(),
                        view.getStoreId(),
                        view.getLastUpdated()
                )
        );
    }

    @Override
    public void delete(PricePointStore pricePointStore) {
        this.ppsRepo.delete(pricePointStore);
    }
}
