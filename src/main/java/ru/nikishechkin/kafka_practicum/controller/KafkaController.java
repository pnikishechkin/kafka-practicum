package ru.nikishechkin.kafka_practicum.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.nikishechkin.kafka_practicum.dto.Message;
import ru.nikishechkin.kafka_practicum.service.KafkaProducerService;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

//    @PostMapping("/send")
//    public String postMessage(@RequestParam String key,
//                              @RequestParam Long number,
//                              @RequestParam String message) {
//        Message messageObj = new Message(number, message);
//        kafkaProducerService.sendToPartition(key, messageObj, 0);
//
//        return "Message sent: " + messageObj;
//    }

    @PostMapping("/send")
    public String postMessage(@RequestParam String key,
            @RequestBody Message message) {
        kafkaProducerService.sendToPartition(key, message, 0);
        return "Message sent: " + message;
    }
}
