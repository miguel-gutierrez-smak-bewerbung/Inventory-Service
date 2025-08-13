package de.resume.inventory.management.system.inventoryservice.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "warehouse", schema = "local", uniqueConstraints = @UniqueConstraint(name = "unique_warehouse_name", columnNames = "name"))
@EqualsAndHashCode(callSuper = true)
public class WarehouseEntity extends BaseEntity {

    @NotBlank(message = "Warehouse name is mandatory")
    @Size(max = 255, message = "Warehouse name must not exceed 255 characters")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Street is mandatory")
    @Size(max = 255, message = "Street must not exceed 255 characters")
    @Column(name = "street")
    private String street;

    @NotBlank(message = "ZIP code is mandatory")
    @Size(max = 20, message = "ZIP code must not exceed 20 characters")
    @Column(name = "zip", length = 20)
    private String zip;

    @NotBlank(message = "City is mandatory")
    @Size(max = 255, message = "City must not exceed 255 characters")
    @Column(name = "city")
    private String city;

    @NotBlank(message = "Country is mandatory")
    @Size(max = 255, message = "Country must not exceed 255 characters")
    @Column(name = "country")
    private String country;
}
