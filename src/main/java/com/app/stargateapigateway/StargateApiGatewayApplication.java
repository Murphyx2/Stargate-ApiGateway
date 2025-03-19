package com.app.stargateapigateway;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class StargateApiGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(StargateApiGatewayApplication.class);

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.directory("./")
				.ignoreIfMissing()
				.load();

		// Set environmental variables as system properties
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(StargateApiGatewayApplication.class, args);
	}

	@PostConstruct
	public void checkNetworkConnectivity() {
		try {
			InetAddress authAddr = InetAddress.getByName("auth");
			logger.info("Resolved auth service: {}", authAddr.getHostAddress());
		} catch (Exception e) {
			logger.error("Failed to resolve auth service", e);
		}
		try {
			InetAddress profileAddr = InetAddress.getByName("profile-service");
			logger.info("Resolved profile-service: {}", profileAddr.getHostAddress());
		} catch (Exception e) {
			logger.error("Failed to resolve profile-service", e);
		}
	}

}
