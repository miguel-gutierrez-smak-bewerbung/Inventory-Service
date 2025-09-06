package de.resume.inventory.management.system.inventoryservice.services.article

import com.fasterxml.jackson.databind.ObjectMapper
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.stereotype.Service

@Service
internal class ArticleServiceImpl(
    val objectMapper: ObjectMapper
): ArticleService {
    override fun consume(record: ConsumerRecord<String, String>) {
        val productUpsertedEvent = objectMapper.read(record.value(), ProductUpsertedEvent::class)
        TODO("Not yet implemented")
    }
}
