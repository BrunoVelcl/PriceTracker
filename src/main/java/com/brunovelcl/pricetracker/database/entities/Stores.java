package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Data
@Table(name = "stores")
public class Stores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String address;

    @ManyToOne
    @JoinColumn(name = "chain_id", nullable = false)
    private Chain chain;

    @Column(name = "last_update", nullable = false)
    private Instant lastParsed;

    public Stores(String address, Chain chain, Instant lastParsed) {
        this.address = address;
        this.chain = chain;
        this.lastParsed = lastParsed;
    }
}
