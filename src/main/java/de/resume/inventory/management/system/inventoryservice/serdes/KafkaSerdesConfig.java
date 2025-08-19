package de.resume.inventory.management.system.inventoryservice.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductDeletedEvent;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
public class KafkaSerdesConfig {

    @Bean
    public ObjectMapper kafkaObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public Serde<String> stringKeySerde() {
        return Serdes.String();
    }

    @Bean
    public Serde<ProductUpsertedEvent> upsertSerde(final ObjectMapper objectMapper) {
        final JsonSerde<ProductUpsertedEvent> productUpsertSerde = new JsonSerde<>(ProductUpsertedEvent.class, objectMapper);
        productUpsertSerde.deserializer().addTrustedPackages("de.resume.inventory.management.system.*");
        productUpsertSerde.deserializer().setUseTypeHeaders(false);
        productUpsertSerde.deserializer().setRemoveTypeHeaders(true);
        return productUpsertSerde;
    }

    @Bean
    public Serde<ProductDeletedEvent> deleteSerde(final ObjectMapper objectMapper) {
        final JsonSerde<ProductDeletedEvent> productDeleteSerde = new JsonSerde<>(ProductDeletedEvent.class, objectMapper);
        productDeleteSerde.deserializer().addTrustedPackages("de.resume.inventory.management.system.*");
        productDeleteSerde.deserializer().setUseTypeHeaders(false);
        productDeleteSerde.deserializer().setRemoveTypeHeaders(true);
        return productDeleteSerde;
    }
}
