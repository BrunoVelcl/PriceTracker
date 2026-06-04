package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "price_point_store")
public class PricePointStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pricePointId;

    private Integer storeId;

    @Setter
    private Instant lastUpdated;

    public PricePointStore(Long pricePointId, Integer storeId) {
        this.pricePointId = pricePointId;
        this.storeId = storeId;
        this.lastUpdated = Instant.now();
    }

}
