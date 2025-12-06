package com.campus.timebank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Campus TimeBank Application
 * 
 * <p>A time-based skill exchange platform for University of Debrecen.
 * This application enables students to offer services, make bookings,
 * and transfer time hours between users using a secure JWT-based authentication system.</p>
 * 
 * <p>Key Features:
 * <ul>
 *   <li>User registration and authentication with JWT tokens</li>
 *   <li>Time-based wallet system with initial balance of 10 hours</li>
 *   <li>Service offers and booking management</li>
 *   <li>Transaction history and audit trail</li>
 *   <li>Role-based access control (User, Admin)</li>
 * </ul>
 * </p>
 * 
 * @author Campus TimeBank Team
 * @version 0.0.1-SNAPSHOT
 * @since 1.0
 */
@Slf4j
@SpringBootApplication
public class CampusTimeBankApplication {

    /**
     * Main entry point for the Spring Boot application.
     * 
     * <p>Initializes the Spring application context, starts embedded servers,
     * and logs application startup information including active profiles
     * and server port.</p>
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CampusTimeBankApplication.class);
        Environment env = app.run(args).getEnvironment();
        
        logApplicationStartup(env);
    }
    
    /**
     * Logs application startup information including active profiles,
     * server port, and local access URLs.
     * 
     * @param env The Spring Environment containing configuration properties
     */
    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";
        
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://{}:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name", "Campus TimeBank"),
                protocol, hostAddress, serverPort, contextPath,
                protocol, hostAddress, serverPort, contextPath,
                Arrays.toString(env.getActiveProfiles().length == 0 
                    ? env.getDefaultProfiles() 
                    : env.getActiveProfiles()));
        
        log.info("Application started successfully with profile(s): {}", 
            Arrays.toString(env.getActiveProfiles().length == 0 
                ? env.getDefaultProfiles() 
                : env.getActiveProfiles()));
    }
}
