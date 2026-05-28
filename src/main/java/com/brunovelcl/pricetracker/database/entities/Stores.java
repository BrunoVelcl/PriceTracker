package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
