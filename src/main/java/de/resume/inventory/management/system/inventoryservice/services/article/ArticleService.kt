package de.resume.inventory.management.system.inventoryservice.services.article

import org.apache.kafka.clients.consumer.ConsumerRecord

fun interface ArticleService {
    fun consume(record: ConsumerRecord<String, String>
    )
}
