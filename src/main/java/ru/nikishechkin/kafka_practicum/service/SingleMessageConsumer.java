package ru.nikishechkin.kafka_practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.nikishechkin.kafka_practicum.dto.Message;

@Service
public class SingleMessageConsumer {

    private final Logger logger = LoggerFactory.getLogger(SingleMessageConsumer.class);

    @KafkaListener(
            topics = "my-topic",
            // groupId = "single-consumer-group",
            containerFactory = "fastKafkaListenerContainerFactory" // механизм для создания различных конфигураций Kafka Listeners в одном приложении
    )
    public void receiveMessage(
            // Тело сообщения - @Payload явно указывает, что параметр должен быть извлечен из тела сообщения Kafka
            @Payload Message message,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,      // Ключ
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, // Партиция
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            logger.info("[ПОЛУЧЕНО СООБЩЕНИЕ] Ключ: {} | Партиция: {} | Смещение: {} | Сообщение: {}", key, partition, offset, message);

            // Ручной коммит оффсета сообщения
            // acknowledgment.acknowledge();

        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage());
            // TODO Отправить сообщение в DLQ (Dead Letter Queue)
        }
    }
}
