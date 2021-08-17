package com.keti.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.context.ApplicationPidFileWriter;


@EnableScheduling
@SpringBootApplication
public class KetiCollectorWeatherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiCollectorWeatherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
