package com.keti.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;


@SpringBootApplication
public class KetiLauncherWeatherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiLauncherWeatherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
