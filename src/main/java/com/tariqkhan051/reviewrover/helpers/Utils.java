package com.tariqkhan051.reviewrover.helpers;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Year;

public class Utils {
    
    public static boolean IsNullOrEmpty(String str) {
        if (str == null || str.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean IsNull(Integer num) {
        if (num == null) {
            return true;
        }
        return false;
    }

    public static int getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getCurrentYear() {
        return LocalDate.now().getYear();
    }

    public static boolean IsValidMonth(int month) {
        if (month < 1 || month > 12) {
            return false;
        }
        return true;
    }

    public static boolean IsValidYear(int year) {
        if (year < 2022) {
            return false;
        }
        return true;
    }

    public static Timestamp GetCurrentTimeStamp()
    {
        return new Timestamp(System.currentTimeMillis());
    }

    public static String SafeTrim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replace("  ", "");
    }
}
