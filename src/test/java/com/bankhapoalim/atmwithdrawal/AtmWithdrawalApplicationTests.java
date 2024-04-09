package com.bankhapoalim.atmwithdrawal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@Profile("test")
@SpringBootTest(classes = AtmWithdrawalApplication.class)
class AtmWithdrawalApplicationTests {

    @Test
    void contextLoads() {
        // This method is empty because its purpose is to verify that the application context loads successfully
        // If the context loads without any exceptions, the test is considered successful
    }
}
