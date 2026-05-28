package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.database.entities.Chain;
import com.brunovelcl.pricetracker.database.repositories.ChainsRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.ChainsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChainsServiceImpl implements ChainsService {

    private final ChainsRepository chainsRepository;

    public ChainsServiceImpl(ChainsRepository chainsRepository) {
        this.chainsRepository = chainsRepository;
    }

    public List<Chain> getAllChains() {
        return chainsRepository.findAll();
    }
}
