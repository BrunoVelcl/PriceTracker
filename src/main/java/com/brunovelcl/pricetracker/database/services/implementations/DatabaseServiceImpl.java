package com.brunovelcl.pricetracker.database.services.implementations;

import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.repositories.DatabaseRepository;
import com.brunovelcl.pricetracker.database.services.interfaces.DatabaseService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final DatabaseRepository dbRepo;

    public DatabaseServiceImpl(DatabaseRepository dbRepo) {
        this.dbRepo = dbRepo;
    }

    @Override
    public void swapTables(String firstTable, String firstTableFK, String secondTable) throws Exception {
        Optional<String> connameOpt = this.dbRepo.findConstraintName(firstTable, secondTable);
        if(connameOpt.isEmpty()){
            System.out.printf(Text.ErrorMessages.FAILED_TO_SWAP_TABLES, firstTable, secondTable);
            return;
        }
        String conname = connameOpt.get();
        this.dbRepo.dropFKConstraintByConname(firstTable, conname);
        this.dbRepo.addFKConstraintByConname(secondTable, conname, firstTableFK, secondTable);
        this.dbRepo.swapTwoTablesWithDrop(firstTable, secondTable);
    }
}
