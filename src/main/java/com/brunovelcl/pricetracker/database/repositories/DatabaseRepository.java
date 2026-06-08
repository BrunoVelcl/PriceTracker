package com.brunovelcl.pricetracker.database.repositories;

import java.util.Optional;

public interface DatabaseRepository {
    void createTableBySchemaCopy(String tempTableName, String existingTableName);
    void swapTwoTablesWithDrop(String oldTable, String newTable);
    Optional<String> findConstraintName(String firstTable, String secondTable);
    void dropFKConstraintByConname(String table, String conname);
    void addFKConstraintByConname(String table, String conname, String firstTablesFK, String secondTable);
}
