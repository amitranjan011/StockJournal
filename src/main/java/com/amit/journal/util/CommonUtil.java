package com.amit.journal.util;

import com.amit.journal.config.PropertyReader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public class CommonUtil {
    private static final Logger LOG = LogManager.getLogger(CommonUtil.class);
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_FORMAT_DDMMYYYY = "ddMMyyyy";

    public static String getDateString(LocalDate dateObj) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String dateString = dateObj.format(formatter);
        LOG.info("dateString : {} for dateObj  : {}", dateString, dateObj.toString());
        return dateString;
    }

    public static String getDateString(LocalDate dateObj, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
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

    public static String getStartOfWeek(LocalDate dateObj) {
        LocalDate startDayWeek = dateObj.with(ChronoField.DAY_OF_WEEK, 1);
        String dateString = getDateString(startDayWeek, DATE_FORMAT_DDMMYYYY);
        LOG.info("startDayWeek : {} for dateObj  : {}", dateString, startDayWeek.toString());
        return dateString;
    }

    public static String getStartOfMonth(LocalDate dateObj) {
        LocalDate startDayMonth = dateObj.with(ChronoField.DAY_OF_MONTH, 1);
        String dateString = getDateString(startDayMonth, DATE_FORMAT_DDMMYYYY);
        LOG.info("startDayMonth : {} for dateObj  : {}", dateString, startDayMonth.toString());
        return dateString;
    }

    public static String getStartOfDay(LocalDate dateObj) {
        String dateString = getDateString(dateObj, DATE_FORMAT_DDMMYYYY);
        LOG.info("dateString : {} for dateObj  : {}", dateString, dateObj.toString());
        return dateString;
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

    public static double round(double value, int places) {
        try {
            if (places < 0) places = 2;

            BigDecimal bd = new BigDecimal(Double.toString(value));
            bd = bd.setScale(places, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (Exception e) {
//            LOG.error("Exception rounding : {}, {}", value, CommonUtil.getStackTrace(e));
            return 0;
        }
    }

    public static double getDouble(String number) {
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            LOG.error("Exception converting : {}, {}", number, CommonUtil.getStackTrace(e));
            return 0;
        }
    }
}
