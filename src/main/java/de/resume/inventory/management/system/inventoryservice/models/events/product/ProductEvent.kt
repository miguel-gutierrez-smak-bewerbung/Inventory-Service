package de.resume.inventory.management.system.inventoryservice.models.events.product

import com.fasterxml.jackson.annotation.JsonProperty
import de.resume.inventory.management.system.inventoryservice.models.enums.ProductAction

abstract class ProductEvent(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("productAction")
    val productAction: ProductAction
) { }
