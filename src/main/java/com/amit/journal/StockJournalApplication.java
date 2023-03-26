package com.amit.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages="com.amit.journal")

public class StockJournalApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockJournalApplication.class, args);
	}

	@Bean
	public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
		return new MongoTransactionManager(dbFactory);
	}
}
