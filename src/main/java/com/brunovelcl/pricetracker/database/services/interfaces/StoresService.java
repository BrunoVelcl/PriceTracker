package com.brunovelcl.pricetracker.database.services.interfaces;

import com.brunovelcl.pricetracker.database.entities.Stores;

import java.util.Optional;

public interface StoresService {
    Optional<Stores> findByAddress(String address);
    Stores save(Stores newStore);
}
