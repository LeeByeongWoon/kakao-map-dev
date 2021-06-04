package com.keti.kafka.producer.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;


@SpringBootApplication
@EnableScheduling
public class KetiProducerWeatherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiProducerWeatherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}