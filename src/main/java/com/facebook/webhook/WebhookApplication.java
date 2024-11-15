package com.facebook.webhook;

import io.github.cdimascio.dotenv.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebhookApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(WebhookApplication.class, args);
	}

	private static void loadEnvVariables() {
		Dotenv.configure()
				.directory(".")
				.filename("application.env")
				.ignoreIfMissing()
				.load()
				.entries();
	}

}
