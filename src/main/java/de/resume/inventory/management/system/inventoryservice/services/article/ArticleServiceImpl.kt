package de.resume.inventory.management.system.inventoryservice.services.article

import com.fasterxml.jackson.databind.ObjectMapper
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent
import org.springframework.stereotype.Service

@Service
internal class ArticleServiceImpl(
    val objectMapper: ObjectMapper
): ArticleService {
    override fun consume(key: String, payload: String) {
        val productUpsertEvent = objectMapper.readValue(payload, ProductUpsertedEvent::class::class.java)
        TODO("Not yet implemented")
    }
}
