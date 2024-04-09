package com.bankhapoalim.atmwithdrawal.util;

import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.time.LocalDate;

@Component
public class DateUtils {
    public Timestamp convertToTimestamp(LocalDate localDate) {
        return Timestamp.valueOf(localDate.atStartOfDay());
    }
}
