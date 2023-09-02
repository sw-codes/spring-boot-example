package com.swcode;

import com.github.javafaker.Faker;
import com.swcode.customer.Customer;
import com.swcode.customer.CustomerRepository;
import com.swcode.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            var faker = new Faker();
            var name = faker.name();
            Random random = new Random();
            String firstName = name.firstName();
            String lastName = name.lastName();
            int age = random.nextInt(16, 99);
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com",
                    passwordEncoder.encode(UUID.randomUUID().toString()),
                    age,
                    Gender.MALE);

            customerRepository.save(customer);
        };
    }

}
