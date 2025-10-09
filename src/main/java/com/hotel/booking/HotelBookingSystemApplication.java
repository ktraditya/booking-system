package com.hotel.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HotelBookingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingSystemApplication.class, args);
        System.out.println("\n===========================================");
        System.out.println("Hotel Booking System API is running!");
        System.out.println("===========================================");
        System.out.println("API Base URL: http://localhost:8080/api/v1");
        System.out.println("Swagger UI: http://localhost:8080/api/v1/swagger-ui.html");
        System.out.println("H2 Console: http://localhost:8080/api/v1/h2-console");
        System.out.println("===========================================\n");
    }
}
