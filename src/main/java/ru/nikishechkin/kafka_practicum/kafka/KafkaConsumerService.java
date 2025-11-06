package ru.nikishechkin.kafka_practicum.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.nikishechkin.kafka_practicum.dto.Message;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "my-topic", groupId = "my-consumer-group")
    public void receiveMessage(@Payload Message message) {
        try {
            System.out.println("Получено сообщение: " + message);
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());

            // TODO Отправить сообщение в DLQ (Dead Letter Queue)
        }
    }
}
