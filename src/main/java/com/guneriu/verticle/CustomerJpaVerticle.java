package com.guneriu.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guneriu.model.Customer;
import com.guneriu.model.CustomerEvent;
import com.guneriu.repository.CustomerRepository;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by ugur on 20.05.2016.
 */
@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CustomerJpaVerticle {

    private Vertx vertx;

    private ObjectMapper objectMapper;

    private CustomerRepository customerRepository;

    @PostConstruct
    private void start() {
        vertx.eventBus().consumer(CustomerEvent.GETALL.name(), this::getAll);
        vertx.eventBus().consumer(CustomerEvent.GET.name(), this::get);
        vertx.eventBus().consumer(CustomerEvent.CREATE.name(), this::save);
        vertx.eventBus().consumer(CustomerEvent.DELETE.name(), this::delete);
    }

    private <T> void getAll(Message<T> message) {
        try {
            log.info("received {} request", message.address());
            message.reply(objectMapper.writeValueAsString(customerRepository.findAll()));
        } catch (JsonProcessingException e) {
            errorResponse(message, e);
        }
    }

    private <T> void get(Message<T> message) {
        try {
            log.info("received {} request", message.address());
            message.reply(objectMapper.writeValueAsString(customerRepository.findOne(Long.valueOf(message.body().toString()))));
        } catch (JsonProcessingException e) {
            errorResponse(message, e);
        }
    }

    private <T> void delete(Message<T> message) {
        log.info("received {} request", message.address());
        customerRepository.delete(Long.valueOf(message.body().toString()));
        message.reply(null);
    }

    private <T> void save(Message<T> message) {
        log.info("received {} request with data {}", message.address(), message.body());
        try {
            Customer customer = customerRepository.save(objectMapper.readValue(message.body().toString(), Customer.class));
            message.reply(objectMapper.writeValueAsString(customer));
        } catch (IOException e) {
            errorResponse(message, e);
        }
    }

    private <T> void errorResponse(Message<T> message, Exception e) {
        log.error("could not serialize object", e);
        message.fail(0, e.getMessage());
    }


}
