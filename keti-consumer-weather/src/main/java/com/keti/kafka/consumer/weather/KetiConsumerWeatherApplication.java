package com.keti.kafka.consumer.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class KetiConsumerWeatherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiConsumerWeatherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
