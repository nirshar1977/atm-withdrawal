package com.bankhapoalim.atmwithdrawal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
@EntityScan(basePackages = "com.bankhapoalim.atmwithdrawal.entity")
public class AtmWithdrawalApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtmWithdrawalApplication.class, args);
    }

}
