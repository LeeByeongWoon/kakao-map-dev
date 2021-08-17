package com.keti.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;


@SpringBootApplication
public class KetiLauncherNewsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiLauncherNewsApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
