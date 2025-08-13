package de.resume.inventory.management.system.inventoryservice.models.entities;

import de.resume.inventory.management.system.inventoryservice.models.enums.Category;
import de.resume.inventory.management.system.inventoryservice.models.enums.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@Table(
        name = "article", schema = "local", uniqueConstraints = @UniqueConstraint(
        name = "unique_article_number_and_unit", columnNames = {"article_number","unit"}
))
@EqualsAndHashCode(callSuper = true)
public class ArticleEntity extends BaseEntity {

    @NotNull(message = "Article number must not be null")
    @NotBlank(message = "Article number is mandatory")
    @Size(max = 255)
    @Column(name = "article_number")
    private String articleNumber;

    @NotNull(message = "Unit is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "unit", length = 64)
    private Unit unit;

    @NotNull(message = "Category is mandatory")
    @Enumerated(EnumType.STRING)
    @Size(max = 255)
    @Column(name = "category")
    private Category category;


    @NotNull(message = "Quantity is mandatory")
    @PositiveOrZero
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull(message = "Name must not be null")
    @NotBlank(message = "Name is mandatory")
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 2048)
    @Column(name = "description", length = 2048)
    private String description;
}