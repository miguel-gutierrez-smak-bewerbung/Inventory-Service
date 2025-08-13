package de.resume.inventory.management.system.inventoryservice.models.entities;

import jakarta.validation.constraints.*;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "reservation", schema = "local", uniqueConstraints = @UniqueConstraint(name = "unique_reservation_event", columnNames = "event_id"))
@EqualsAndHashCode(callSuper = true)
public class ReservationEntity extends BaseEntity {
    @NotNull(message = "Article reference is mandatory")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    @NotNull(message = "Warehouse reference is mandatory")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;

    @NotNull(message = "Reserved quantity is mandatory")
    @PositiveOrZero(message = "Reserved quantity must be zero or positive")
    @Column(name = "reserved_quantity")
    private Long reservedQuantity;

    @NotBlank(message = "Status is mandatory")
    @Size(max = 64)
    @Column(name = "status")
    private String status;

    @NotBlank(message = "Event ID is mandatory")
    @Size(max = 255)
    @Column(name = "event_id")
    private String eventId;
}
