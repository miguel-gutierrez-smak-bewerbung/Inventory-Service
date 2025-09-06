package de.resume.inventory.management.system.inventoryservice.models.events.product

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import de.resume.inventory.management.system.inventoryservice.models.enums.ProductAction
import java.time.LocalDateTime

class ProductUpsertedEvent(

    id: String,
    productAction: ProductAction,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("articleNumber")
    val articleNumber: String,

    @field:JsonProperty("category")
    val category: String,

    @field:JsonProperty("unit")
    val unit: String,

    @field:JsonProperty("price")
    val price: Double,

    @field:JsonProperty("description")
    val description: String,

    @field:JsonProperty("timestamp")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val timestamp: LocalDateTime,

    @field:JsonProperty("tenantId")
    val tenantId: String
) : ProductEvent(id, productAction)
