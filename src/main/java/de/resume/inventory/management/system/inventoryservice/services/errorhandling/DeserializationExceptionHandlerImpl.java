package de.resume.inventory.management.system.inventoryservice.services.errorhandling;

import de.resume.inventory.management.system.inventoryservice.config.ArticleTopicConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.errors.ErrorHandlerContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class DeserializationExceptionHandlerImpl implements DeserializationExceptionHandler, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private ArticleTopicConfig articleTopicConfig;
    private KafkaProducer<String, String> failTopicProducer;


    @Override
    public void configure(final Map<String, ?> map) {
        articleTopicConfig = applicationContext.getBean(ArticleTopicConfig.class);
        failTopicProducer = applicationContext.getBean("kafkaErrorProducer", KafkaProducer.class);
    }

    @Override
    public void setApplicationContext(@NotNull final ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    @Override
    public DeserializationHandlerResponse handle(final ErrorHandlerContext context, final ConsumerRecord<byte[], byte[]> record, final Exception exception) {
        log.error("Error while deserializing record: {}", record, exception);
        final String payload = getRawPayload(record);
        final String key = getKeyAsString(record);
        final ProducerRecord<String, String> failRecord = new ProducerRecord<>(articleTopicConfig.getFail(),key, payload);
        failTopicProducer.send(failRecord);
        return DeserializationHandlerResponse.CONTINUE;
    }

    private String getRawPayload(final ConsumerRecord<byte[], byte[]> consumerRecord) {
        if (Objects.isNull(consumerRecord) || Objects.isNull(consumerRecord.value())) {
            log.error("DeserializationExceptionHandler: ConsumerRecord or its value is null, cannot serialize to Base64");
            return "<null>";
        }
        return encodeConsumerRecordValue(consumerRecord);
    }

    private String getKeyAsString(final ConsumerRecord<byte[], byte[]> record) {
        return Optional.ofNullable(record.key())
                .map(rawKey -> new String(rawKey, StandardCharsets.UTF_8))
                .orElseThrow(IllegalArgumentException::new);
    }

    private String encodeConsumerRecordValue(ConsumerRecord<byte[], byte[]> consumerRecord) {
        return Base64.getEncoder().encodeToString(consumerRecord.value());
    }

}
