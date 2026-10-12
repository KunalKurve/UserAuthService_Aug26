package dev.kunal.userauthservice_aug26.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerClient {

    //brother class of RestTemplate, which comes from Spring Kafka dependency
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {

        // Kafka inbuilt method to send message as a Producer into Kafka topic
        kafkaTemplate.send(topic, message);
    }
}
