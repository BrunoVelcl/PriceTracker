package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.database.entities.Stores;
import com.brunovelcl.pricetracker.database.repositories.StoresRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.StoresService;

import java.util.Optional;

public class StoresServiceImpl implements StoresService {

    private final StoresRepository storesRepository;

    public StoresServiceImpl(StoresRepository storesRepository) {
        this.storesRepository = storesRepository;
    }

    @Override
    public Optional<Stores> findByAddress(String address) {
        return this.storesRepository.findByAddress(address);
    }

    @Override
    public Stores save(Stores newStore) {
        return this.storesRepository.save(newStore);
    }
}
