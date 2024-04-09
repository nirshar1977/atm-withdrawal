package com.bankhapoalim.atmwithdrawal.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig
@ActiveProfiles("test")
class ApplicationTest {

    @Autowired
    private Environment environment;

    @Test
    void testActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();

        Assertions.assertNotNull(activeProfiles);
        Assertions.assertTrue(activeProfiles.length > 0);

        boolean testProfileFound = false;
        for (String profile : activeProfiles) {
            System.out.println("Active Profile: " + profile);
            if ("test".equals(profile)) {
                testProfileFound = true;
                break;
            }
        }

        Assertions.assertTrue(testProfileFound, "Test profile 'test' not found in active profiles.");
    }
}