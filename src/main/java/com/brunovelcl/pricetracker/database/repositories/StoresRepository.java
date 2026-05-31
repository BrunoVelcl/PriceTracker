package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.database.entities.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface StoresRepository extends JpaRepository<Stores, Integer> {
    Optional<Stores> findByAddress(String address);
}
