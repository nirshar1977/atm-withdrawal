package com.bankhapoalim.atmwithdrawal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
@EntityScan("com.bankhapoalim.atmwithdrawal.entity")
@ComponentScan(basePackages = {"com.bankhapoalim.atmwithdrawal.config"})
public class AtmWithdrawalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmWithdrawalApplication.class, args);
    }
}
