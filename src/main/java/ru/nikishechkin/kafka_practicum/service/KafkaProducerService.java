package ru.nikishechkin.kafka_practicum.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.nikishechkin.kafka_practicum.dto.Message;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "my-topic";
    private final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(String key, Message message) {
        logger.info("[ОТПРАВКА СООБЩЕНИЯ]: {}  в топик {}", message, TOPIC);
        kafkaTemplate.send(TOPIC, key, message);
    }

    public void sendToPartition(String key, Message message, int partition) {
        logger.info("[ОТПРАВКА СООБЩЕНИЯ]: {} в топик {}, партиция: {}", message, TOPIC, partition);
        kafkaTemplate.send(TOPIC, partition, key, message);
    }
}
