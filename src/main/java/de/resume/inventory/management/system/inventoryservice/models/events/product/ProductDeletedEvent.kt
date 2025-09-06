package de.resume.inventory.management.system.inventoryservice.models.events.product

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import de.resume.inventory.management.system.inventoryservice.models.enums.ProductAction
import java.time.LocalDateTime

class ProductDeletedEvent(
    id: String,
    productAction: ProductAction,

    @field:JsonProperty("timestamp")
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val timestamp: LocalDateTime,

    @field:JsonProperty("tenantId")
    val tenantId: String
) : ProductEvent(id, productAction) {


}

