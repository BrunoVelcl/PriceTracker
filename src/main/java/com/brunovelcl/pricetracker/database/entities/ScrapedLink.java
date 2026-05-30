package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@NoArgsConstructor
@Entity
@Data
@Table(name = "scraped_links")
public class ScrapedLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String link;

    @Column(nullable = false, updatable = false, unique = true)
    private String filename;

    @Column(nullable = false, updatable = false)
    private Instant found;

    @Column(nullable = false)
    private Boolean processed;

    @ManyToOne
    @JoinColumn(name = "chain_id", nullable = false)
    private Chain chain;

    public ScrapedLink(String link, String filename, Chain chain) {
        this.link = link;
        this.filename = filename;
        this.processed = false;
        this.chain = chain;
    }

}
