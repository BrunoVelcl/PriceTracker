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
public interface ScraoedLinksRepository extends JpaRepository<ScrapedLink, Long> {

    @Modifying
    @Transactional
    @Query(
            value = """
                    INSERT INTO scrapedLinks (link, filename)
                    VALUES(:link, :filename)
                    ON CONFLICT DO  NOTHING
                    """,
            nativeQuery = true
    )
    void insertIgnoreDuplicate(@Param("link") String link, @Param("filename") String filename);

    List<ScrapedLink> findByProcessedFalse();

    @Modifying
    @Transactional
    @Query(
            value = """
                    UPDATE scrapedLinks
                    SET processed=true
                    WHERE id = :id
                    """,
            nativeQuery = true
    )
    int processedSuccessfully(@Param("id") Long id);
}
