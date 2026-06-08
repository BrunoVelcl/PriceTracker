package com.brunovelcl.pricetracker.database.repositories.implementations;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointRowDTO;
import com.brunovelcl.pricetracker.database.repositories.PricePointRepositoryCustom;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.sql.Connection;
import java.util.List;

@Repository
public class PricePointRepositoryCustomImpl implements PricePointRepositoryCustom {

    private final DataSource dataSource;

    public PricePointRepositoryCustomImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String DELIMITER = ",";

    @Override
    public void ingestTable(List<PricePointRowDTO> list, String tableName) throws Exception {

        final String sql = String.format("""
                    COPY %s (id, price_euros, brand_name_product_id)
                    FROM STDIN WITH (FORMAT CSV)
                    """, tableName);
        try(Connection con = dataSource.getConnection();
            PipedWriter pipedWriter = new PipedWriter()){

            PipedReader pipedReader = new PipedReader(pipedWriter, 1024*64);
            PGConnection pgCon = con.unwrap(PGConnection.class);
            CopyManager copyManager = pgCon.getCopyAPI();

            Thread writerThread = new Thread(() ->{
                try(BufferedWriter buffWriter = new BufferedWriter(pipedWriter)){
                    for(PricePointRowDTO dto : list){
                        buffWriter.write(dto.getId() + DELIMITER + dto.getPrice() + DELIMITER + dto.getProductId());
                        buffWriter.newLine();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            writerThread.start();
            copyManager.copyIn(sql, pipedReader);
            writerThread.join();
        }
    }
}
