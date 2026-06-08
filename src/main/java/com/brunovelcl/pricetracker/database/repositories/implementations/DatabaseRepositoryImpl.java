package com.brunovelcl.pricetracker.database.repositories.implementations;

import com.brunovelcl.pricetracker.database.repositories.DatabaseRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseRepositoryImpl implements DatabaseRepository {

    private final EntityManager em;

    public DatabaseRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Transactional
    @Override
    public void createTableBySchemaCopy(String tempTableName, String existingTableName) {
        final String sql = String.format("""
                        CREATE TABLE IF NOT EXISTS %s(
                        LIKE %s INCLUDING ALL
                        )
                        """, tempTableName, existingTableName);
        em.createNativeQuery(sql).executeUpdate();
    }

    @Transactional
    @Override
    public void swapTwoTablesWithDrop(String oldTable, String newTable) {
        final String sql = String.format("""
                DO $$
                BEGIN
                    ALTER TABLE %s RENAME TO %s_old;
                    ALTER TABLE %s RENAME TO %s;
                    DROP TABLE %s_old;
                END $$
                """, oldTable, oldTable, newTable, oldTable, oldTable);

        em.createNativeQuery(sql).executeUpdate();
    }

    @Override
    public Optional<String> findConstraintName(String firstTable, String secondTable) {
        final String sql = String.format("""
                SELECT conname FROM pg_constraint
                WHERE conrelid = '%s'::regclass
                AND confrelid = '%s'::regclass
                AND contype = 'f'
                """, firstTable, secondTable);

        @SuppressWarnings("unchecked") // conname is allways a String
        List<String> connameList = this.em.createNativeQuery(sql).getResultList();
        if(connameList.size() != 1) return Optional.empty();
        return Optional.of(connameList.getFirst());
    }

    @Override
    public void dropFKConstraintByConname(String table, String conname) {
        final String sql = String.format("""
                ALTER TABLE %s
                DROP CONSTRAINT %s
                """, table, conname);
    }

    @Override
    public void addFKConstraintByConname(String table, String conname, String firstTablesFK, String secondTable) {
        final String sql = String.format("""
                ALTER TABLE %s
                ADD CONSTRAINT %s
                FOREIGN KEY (%s) REFERENCES price
                """, table, conname, firstTablesFK, secondTable);
    }
}
