package de.resume.inventory.management.system.inventoryservice.kafka.listener

import de.resume.inventory.management.system.inventoryservice.config.ArticleTopicConfig
import de.resume.inventory.management.system.inventoryservice.services.article.ArticleService
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Service

@Service
class ProductSnapshotListener(
    private val articleTopicConfig: ArticleTopicConfig,
    private val articleService: ArticleService
) {

    val log = KotlinLogging.logger {  }

    @KafkaListener(
        topics = ["#{@articleTopicConfig.productsSnapshot}"],
        groupId= "\${spring.kafka.consumer.group-id}"
    )
    fun onConsume(record: ConsumerRecord<String, String?>, acknowledgment: Acknowledgment) {
        log.info("[ProductSnapshotListener] start consuming message: ${record.value()} and kafkaKey ${record.key()}")
        //todo is already processed
        record.value()?.let {
            articleService.consume(record.key(),it)

            return
        }
        log.info("tombstone message for key ${record.key()}")
        acknowledgment.acknowledge()
    }
}
