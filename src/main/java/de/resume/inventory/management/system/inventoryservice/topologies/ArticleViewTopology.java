package de.resume.inventory.management.system.inventoryservice.topologies;

import de.resume.inventory.management.system.inventoryservice.config.ArticleTopicConfig;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductDeletedEvent;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArticleViewTopology {

    private final Serde<String> stringKeySerde;
    private final Serde<ProductUpsertedEvent> upsertSerde;
    private final Serde<ProductDeletedEvent> deleteSerde;
    private final ArticleTopicConfig articleTopicConfig;

    @Autowired
    public void buildTopology(final StreamsBuilder streamsBuilder) {
        final KTable<String, ProductUpsertedEvent> upsertEvents = createUpsertEventStreamWithGlobalStore(streamsBuilder);
        final KTable<String, ProductDeletedEvent> deletedEvents = createDeleteEventStreamWithGlobalStore(streamsBuilder);

        upsertEvents.toStream().peek(ArticleViewTopology::logUpsertEvent);
        deletedEvents.toStream().peek(ArticleViewTopology::logDeleteEvent);

        upsertEvents.leftJoin(deletedEvents,(upsertEvent, deleteEvent) ->
                Objects.nonNull(deleteEvent) ? null : upsertEvent,
                Materialized.with(stringKeySerde, upsertSerde)
        ).toStream()
                .peek(((key, productUpsertedEvent) -> log.info("Join: Product upserted event: {}", productUpsertedEvent)))
                .to(articleTopicConfig.getProductsSnapshot(), Produced.with(stringKeySerde, upsertSerde));
    }

    private KTable<String, ProductDeletedEvent> createDeleteEventStreamWithGlobalStore(StreamsBuilder streamsBuilder) {
        return streamsBuilder.table(
                articleTopicConfig.getDelete(),
                Consumed.with(stringKeySerde, deleteSerde),
                Materialized.as("products-delete-table")
        );
    }

    private KTable<String, ProductUpsertedEvent> createUpsertEventStreamWithGlobalStore(final StreamsBuilder streamsBuilder) {
        return streamsBuilder.table(
                articleTopicConfig.getUpsert(),
                Consumed.with(stringKeySerde, upsertSerde),
                Materialized.as("products-upsert-table")
        );
    }

    private static void logUpsertEvent(final String key, final ProductUpsertedEvent productUpsertedEvent) { //TODO: Logging Service
        if(Objects.nonNull(productUpsertedEvent)) {
            log.info("[UPSERT-TABLE-UPDATE] key={}, value={}", key, productUpsertedEvent);
        }
    }

    private static void logDeleteEvent(final String key, final ProductDeletedEvent productDeletedEvent) {
        if(Objects.nonNull(productDeletedEvent)) {
            log.info("[DELETE-TABLE-UPDATE] key={}, value={}", key, productDeletedEvent);
        }
    }
}
