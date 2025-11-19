package ru.nikishechkin.kafka_practicum.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    // Фабрика простых консьюмеров для обработки большого количества сообщений с максимальной производительностью
    @Bean
    public ConsumerFactory<String, Object> fastConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fast-group"); // отдельная consumer группа
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // за один poll получает до 100 сообщений
        // ENABLE_AUTO_COMMIT_CONFIG не указан - используется значение по умолчанию (обычно true)
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Фабрика для быстрой обработки
    // Создает контейнеры для Listeners, которые используют быструю стратегию
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> fastKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(fastConsumerFactory());
        return factory;
    }

    // ConcurrentKafkaListenerContainerFactory - а есть и другие?

    // Фабрика для создания надежных консьюмеров для обработки критически важных сообщений с гарантированной доставкой
    // @Bean
    public ConsumerFactory<String, Object> reliableConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reliable-group");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1); // обработка по 1 сообщению
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // отключает авто-коммит
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    // Фабрика для надежной обработки
    // Создает контейнеры для Listeners, которые используют надежную стратегию с ручным подтверждением
    // @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> reliableKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(reliableConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Ручное подтверждение получения
        return factory;
    }

}
