package com.bankhapoalim.atmwithdrawal.util;

import java.sql.Timestamp;
import java.time.LocalDate;

public class DateUtils {
    public static Timestamp  convertToTimestamp(LocalDate localDate){
        return Timestamp.valueOf(localDate.atStartOfDay());
    }
}
