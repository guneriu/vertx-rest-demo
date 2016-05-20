package com.guneriu;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guneriu.model.Customer;
import com.guneriu.repository.CustomerRepository;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.core.internal.CustomizerRegistry;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class VertxRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(VertxRestApplication.class, args);
	}

	@Bean
	Vertx vertx () {
		log.info("creating vert.x bean");
		return Vertx.vertx();
	}

	@Bean
	public ObjectMapper objectMapper() {
		log.info("creating object mapper");
		return new ObjectMapper(new JsonFactory());
	}

	@Bean
	CommandLineRunner initializeData(CustomerRepository customerRepository) {
		return args -> {
			customerRepository.save(new Customer("Josh"));
			customerRepository.save(new Customer("David"));
			customerRepository.save(new Customer("Chris"));

			log.info("inserted customers: \n {}", customerRepository.findAll());
		};

	}
}
