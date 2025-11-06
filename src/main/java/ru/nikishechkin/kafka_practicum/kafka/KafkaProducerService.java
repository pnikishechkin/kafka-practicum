package ru.nikishechkin.kafka_practicum.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.nikishechkin.kafka_practicum.dto.Message;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "my-topic";

    @Autowired
    private KafkaTemplate<String, Message> kafkaTemplate;

    public void sendMessage(String key, Message message) {
        kafkaTemplate.send(TOPIC, key, message);
    }

    public void sendToPartition(String key, Message message, int partition) {
        kafkaTemplate.send(TOPIC, partition, key, message);
    }
}
