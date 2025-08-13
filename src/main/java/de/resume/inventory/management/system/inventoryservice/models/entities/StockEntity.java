package de.resume.inventory.management.system.inventoryservice.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "stock", schema = "local", uniqueConstraints = @UniqueConstraint(name = "unique_stock_article_and_warehouse", columnNames = {"article_id", "warehouse_id"}))
@EqualsAndHashCode(callSuper = true)
public class StockEntity extends BaseEntity {

    @NotNull(message = "Article reference is mandatory")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @NotNull(message = "Warehouse reference is mandatory")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @NotNull(message = "Quantity is mandatory")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    @Column(name = "quantity")
    private Long quantity;
}
