package de.resume.inventory.management.system.inventoryservice.services.logging

import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductDeletedEvent
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductEvent
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent
import mu.KotlinLogging


class LoggingService {

    companion object {

        private val log = KotlinLogging.logger {  }

        fun logProductEvent(key: String, productEvent: ProductEvent) {
            when(productEvent) {
                is ProductUpsertedEvent -> log.info("[UPSERT-TABLE-UPDATE] key={}, value={}", key, productEvent)
                is ProductDeletedEvent -> log.info("[DELETE-TABLE-UPDATE] key={}, value={}", key, productEvent);
            }
        }
    }
}
