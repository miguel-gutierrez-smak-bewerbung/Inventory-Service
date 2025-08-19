package de.resume.inventory.management.system.inventoryservice.services.errorhandling;

import de.resume.inventory.management.system.inventoryservice.config.ArticleTopicConfig;
import de.resume.inventory.management.system.inventoryservice.models.events.product.ProductUpsertedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.streams.errors.ProductionExceptionHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ProductionExceptionHandlerImpl implements ProductionExceptionHandler, ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private ArticleTopicConfig articleTopicConfig;
    private KafkaProducer<String, ProductUpsertedEvent> retryFailTopicProducer;

    public ProductionExceptionHandlerImpl(KafkaProducer<String, ProductUpsertedEvent> retryFailTopicProducer) {
        this.retryFailTopicProducer = retryFailTopicProducer;
    }


    @Override
    public void configure(final Map<String, ?> map) {
        articleTopicConfig = applicationContext.getBean(ArticleTopicConfig.class);
        retryFailTopicProducer = applicationContext.getBean("kafkaErrorProducer", KafkaProducer.class);
        articleTopicConfig = applicationContext.getBean(ArticleTopicConfig.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }
}
