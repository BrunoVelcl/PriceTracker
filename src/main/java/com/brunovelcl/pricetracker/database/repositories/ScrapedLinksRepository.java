package com.brunovelcl.pricetracker.database.repositories;

import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapedLinksRepository extends JpaRepository<ScrapedLink, Long> {

    @Modifying
    @Transactional
    @Query(
            value = """
                    INSERT INTO scraped_links (link, filename, processed, chain_id)
                    VALUES(:link, :filename, false, :chain_id)
                    ON CONFLICT DO  NOTHING
                    """,
            nativeQuery = true
    )
    void insertIgnoreDuplicate(@Param("link") String link, @Param("filename") String filename, @Param("chain_id") Integer chainId);

    List<ScrapedLink> findByProcessedFalseAndChainId(Integer chainId);

    @Modifying
    @Transactional
    @Query(
            value = """
                    UPDATE scraped_links
                    SET processed=true
                    WHERE id IN :ids
                    """,
            nativeQuery = true
    )
    int processedSuccessfully(@Param("ids") List<Long> ids);
}
