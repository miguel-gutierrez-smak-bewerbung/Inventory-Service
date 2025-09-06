package de.resume.inventory.management.system.inventoryservice.kafka.topologies;

import de.resume.inventory.management.system.inventoryservice.config.ArticleTopicConfig;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductDeletedEvent;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent;
import de.resume.inventory.management.system.inventoryservice.services.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
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

        upsertEvents.toStream().peek(LoggingService.Companion::logProductEvent);
        deletedEvents.toStream().peek(LoggingService.Companion::logProductEvent);

        upsertEvents.leftJoin(deletedEvents,(upsertEvent, deleteEvent) ->
                Objects.nonNull(deleteEvent) ? null : upsertEvent,
                Materialized.with(stringKeySerde, upsertSerde)
        ).toStream()
                .peek(((key, productUpsertedEvent) -> log.info("Join: Product upserted event: {}", productUpsertedEvent)))
                .to(articleTopicConfig.getProductsSnapshot(), Produced.with(stringKeySerde, upsertSerde));
    }

    private KTable<String, ProductDeletedEvent> createDeleteEventStreamWithGlobalStore(final StreamsBuilder streamsBuilder) {
        return streamsBuilder.table(
                articleTopicConfig.getDelete(),
                Consumed.with(stringKeySerde, deleteSerde),
                Materialized.<String, ProductDeletedEvent, KeyValueStore<Bytes, byte[]>>
                        as("products-delete-table")
                        .withKeySerde(stringKeySerde)
                        .withValueSerde(deleteSerde)
                        .withCachingEnabled()
        );
    }

    private KTable<String, ProductUpsertedEvent> createUpsertEventStreamWithGlobalStore(final StreamsBuilder streamsBuilder) {
        return streamsBuilder.table(
                articleTopicConfig.getUpsert(),
                Consumed.with(stringKeySerde, upsertSerde),
                Materialized.<String, ProductUpsertedEvent, KeyValueStore<Bytes, byte[]>>
                                as("products-upsert-table")
                        .withKeySerde(stringKeySerde)
                        .withValueSerde(upsertSerde)
                        .withCachingEnabled()
        );
    }
}
