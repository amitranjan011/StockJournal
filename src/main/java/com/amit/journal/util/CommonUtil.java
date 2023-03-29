package com.amit.journal.util;

import com.amit.journal.config.PropertyReader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CommonUtil {
    private static final Logger LOG = LogManager.getLogger(CommonUtil.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String getDateString(LocalDate dateObj) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String dateString = dateObj.format(formatter);
        LOG.info("dateString : {} for dateObj  : {}", dateString, dateObj.toString());
        return dateString;
    }

    public static String getHourMinuteString(LocalDate dateObj) {
        LocalDateTime time = LocalDateTime.of(dateObj, LocalTime.now());
        int hour = time.getHour();
        int minute = time.getMinute();
        String hourMinute = CommonUtil.generateId(hour + "", minute + "");
        return hourMinute;
    }

    public static String getTodayDateString() {
        return getDateString(LocalDate.now());
    }

    public static String getUploadDir() {
        String userDir = System.getProperty("user.dir");
        String uploadDir = PropertyReader.getProperty("upload.dir.path", userDir);
        System.out.println("uploadDir == " + uploadDir);
        LOG.info("uploadDir  : {}", uploadDir);
        return uploadDir;
    }

    public static boolean isNullOrEmpty(String ipStr) {
        return ipStr == null || ipStr.isEmpty();
    }

    public static boolean isObjectNullOrEmpty(Object object) {
        return object == null || object.toString().isEmpty();
    }

    public static String getStackTrace(Throwable throwable) {
        return ExceptionUtils.getStackTrace(throwable);
    }

    public static String generateId(String... args) {
        return String.join("-", args);
    }
}
