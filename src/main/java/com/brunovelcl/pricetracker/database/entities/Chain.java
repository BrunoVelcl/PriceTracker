package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Entity
@Data
@Table(name = "chains")
public class Chain {

    public Chain(String name, String webAddress, String priceCatalogWebAddress) {
        this.name = name;
        this.webAddress = webAddress;
        this.priceCatalogWebAddress = priceCatalogWebAddress;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, updatable = false)
    private String name;

    @Column(nullable = false)
    private String webAddress;

    @Column(nullable = false)
    private String priceCatalogWebAddress;

    @OneToMany(mappedBy = "chain")
    private List<Stores> stores;

    @OneToMany(mappedBy = "chain")
    private List<ScrapedLink> scrapedLinks;
}
