package com.gumgum.rozier;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Date-related functions to be used within H2. These functions all replicate functionality available in MySQL.
 *
 * Usage: After setting up your test database, execute statements in this format:
 * `CREATE ALIAS FROM_DAYS FOR "com.gumgum.rozier.DateFunctions.fromDays";`
 *
 * @author skiley on 2018-10-28
 */
@SuppressWarnings("unused")
public final class DateFunctions {

    private static final LocalDateTime ZERO_START_TIME = LocalDateTime.of(0, 1, 1, 0, 0, 0);

    private static final Map<String, String> MYSQL_TO_JAVA_DATE_FORMAT;

    static {
        MYSQL_TO_JAVA_DATE_FORMAT = new HashMap<>();
        MYSQL_TO_JAVA_DATE_FORMAT.put("%a", "E");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%b", "M");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%c", "M");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%d", "dd");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%e", "d");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%f", "S");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%H", "HH");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%h", "H");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%I", "h");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%i", "mm");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%J", "D");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%k", "h");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%l", "h");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%M", "M");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%m", "MM");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%p", "a");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%r", "hh:mm:ss a");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%s", "ss");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%S", "ss");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%T", "HH:mm:ss");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%U", "w");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%u", "w");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%V", "w");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%v", "w");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%W", "EEE");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%w", "F");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%Y", "yyyy");
        MYSQL_TO_JAVA_DATE_FORMAT.put("%y", "yy");
    }

    private DateFunctions() { }

    public static Date date(Timestamp date) {
        return new Date(date.getTime());
    }

    public static LocalDate fromDays(Integer days) {
        return ZERO_START_TIME.plusDays(days).toLocalDate();
    }

    public static Long toDays(Date date) {
        LocalDate startDate = ZERO_START_TIME.toLocalDate();
        LocalDate endDate = date.toLocalDate();
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public static String dateFormat(Timestamp date, String mysqlPattern) {
        String javaPattern = mysqlPattern;
        for (Map.Entry<String, String> entry : MYSQL_TO_JAVA_DATE_FORMAT.entrySet()) {
            javaPattern = javaPattern.replace(entry.getKey(), entry.getValue());
        }
        return DateFormatUtils.format(date, javaPattern);
    }
}
