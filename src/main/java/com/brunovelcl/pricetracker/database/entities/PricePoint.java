package com.brunovelcl.pricetracker.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "price_points")
public class PricePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "price_euros", nullable = false, updatable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false)
    private BrandNameProduct brandNameProduct;

    public PricePoint(BigDecimal price, BrandNameProduct brandNameProduct) {
        this.price = price;
        this.brandNameProduct = brandNameProduct;
    }
}
