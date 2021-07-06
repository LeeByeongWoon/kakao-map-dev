package com.keti.weather.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class KetiWeatherLauncherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiWeatherLauncherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
