package com.brunovelcl.pricetracker.database.services.interfaces;

public interface DatabaseService {
    void swapTables(String firstTable, String firstTableFK, String secondTable) throws Exception;
}
