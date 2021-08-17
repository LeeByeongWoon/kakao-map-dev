package com.keti.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;


@SpringBootApplication
public class KetiCollectorCsvApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiCollectorCsvApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
