package com.brunovelcl.pricetracker.database.repositories.implementations;

import com.brunovelcl.pricetracker.DataWriter.dtos.PricePointStoreDTO;
import com.brunovelcl.pricetracker.database.repositories.PricePointStoreRepositoryCustom;
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
public class PricePointStoreRepositoryCustomImpl implements PricePointStoreRepositoryCustom {

    private final DataSource dataSource;

    public PricePointStoreRepositoryCustomImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String DELIMITER = ",";

    @Override
    public void ingestTable(List<PricePointStoreDTO> list, String tableName) throws Exception {

        final String sql = String.format("""
                    COPY %s (price_point_id, store_id, last_updated)
                    FROM STDIN WITH (FORMAT CSV)
                    """, tableName);

            try(Connection con = dataSource.getConnection();
                PipedWriter pipedWriter = new PipedWriter()){

                PipedReader pipedReader = new PipedReader(pipedWriter, 1024*64);
                PGConnection pgCon = con.unwrap(PGConnection.class);
                CopyManager copyManager = pgCon.getCopyAPI();

                Thread writerThread = new Thread(() ->{
                    try(BufferedWriter buffWriter = new BufferedWriter(pipedWriter)){
                        for(PricePointStoreDTO dto : list){
                            buffWriter.write(dto.getPricePointId() + DELIMITER + dto.getStoreId() + DELIMITER + dto.getLastUpdated());
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
