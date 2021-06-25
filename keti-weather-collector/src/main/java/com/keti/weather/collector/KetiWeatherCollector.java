package com.keti.weather.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@EnableScheduling
@SpringBootApplication
public class KetiWeatherCollector {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiWeatherCollector.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
