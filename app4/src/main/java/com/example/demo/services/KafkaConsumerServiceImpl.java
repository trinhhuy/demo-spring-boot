package com.example.demo.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.example.demo.dto.message.OrderCreatedMessage;
import com.example.demo.enums.OrderStatusEnum;
import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerServiceImpl {
    private final ProductRepository productRepository;
    private final KafkaProducerServiceImpl kafkaProducerService;
    private final PlatformTransactionManager transactionManager;

    public KafkaConsumerServiceImpl(
            ProductRepository productRepository,
            KafkaProducerServiceImpl kafkaProducerService,
            PlatformTransactionManager transactionManager) {
        this.kafkaProducerService = kafkaProducerService;
        this.productRepository = productRepository;
        this.transactionManager = transactionManager;
    }

    @Value("${spring.kafka.producer.topic.payment-success}")
    String paymentSuccessTopic;

    @Value("${spring.kafka.producer.topic.payment-failed}")
    String paymentFailedTopic;

    @KafkaListener(
            topics = "${spring.kafka.producer.topic.order-created}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, String> message) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def); // Bắt đầu transaction

        try {
            System.out.println("BEGIN TRANSACTION");

            log.info("Received message: {}", message.value());
            OrderCreatedMessage order = toOrder(message.value());
            if (order == null) {
                log.error("deserialized error:{}, {}", message.key(), message.value());
                return;
            }

            log.info("id____: {}", order.getId());
            log.info("amount: {}", order.getAmount());
            log.info("status: {}", order.getStatus());

            Product product = productRepository.findById(order.getProductId()).orElse(null);
            if (product == null) {
                log.error("product not found: {}, {}", message.key(), message.value());
                //                kafkaProducerService.sendMessage(paymentFailedTopic, message.key(), message.value());
                //                return;
                throw new RuntimeException("product not found");
            }

            if (product.getQuantity() <= 0) {
                log.error("product not enough quantity: {}, {}", message.key(), message.value());
                throw new RuntimeException("product not enough quantity");
                //                return;
            }

            // get account
            // check amount < balance
            product.setQuantity(product.getQuantity() - 1);
            order.setStatus(OrderStatusEnum.PAYMENT_SUCCESS);
            productRepository.save(product);

            transactionManager.commit(status); // Commit nếu không lỗi
            System.out.println("COMMIT TRANSACTION");

            kafkaProducerService.sendMessage(paymentSuccessTopic, message.key(), order.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
            transactionManager.rollback(status); // Rollback nếu có lỗi
            kafkaProducerService.sendMessage(paymentFailedTopic, message.key(), message.value());
            System.out.println("ROLLBACK TRANSACTION");
        }
    }

    private OrderCreatedMessage toOrder(String jsonStrMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonStrMessage, OrderCreatedMessage.class);
        } catch (JsonProcessingException e) {
            return null;
            //            AppException app = new AppException(ErrorCode.DESERIALIZER_EXCEPTION);
            //            app.setStackTrace(e.getStackTrace());
            //            throw app;
        }
    }
}
