package com.keti.finance.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class KetiFinanceLauncherApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KetiFinanceLauncherApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
