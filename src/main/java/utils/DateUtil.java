package utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author cjl
 * @since 2024/9/22 16:57
 */
public class DateUtil {

    // 日期格式
    public static SimpleDateFormat YYYY_MM_DD_SDF = new SimpleDateFormat("yyyy-MM-dd");
    public static DateTimeFormatter YYYY_MM_DD_DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取系统日期时间
     *
     * @return
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 获取系统日期
     *
     * @return
     */
    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    /**
     * 提供默认的LocalDate转换为String
     *
     * @param localDate
     * @return
     */
    public static String localDateToString(LocalDate localDate) {
        return localDate.format(YYYY_MM_DD_DTF);
    }

    /**
     * 提供默认的String转换为LocalDate
     *
     * @param s
     * @return
     */
    public static LocalDate stringToLocalDate(String s) {
        return LocalDate.parse(s, YYYY_MM_DD_DTF);
    }

    /**
     * 计算两个日期间的天数差
     *
     * @param start 起始时间 格式为yyyy-MM-dd
     * @param end   结束时间 格式为yyyy-MM-dd
     * @return 返回两个日期间隔的天数，同一天返回0
     */
    public static long calYearBetween(String start, String end) {
        LocalDate startDate = LocalDate.parse(start, YYYY_MM_DD_DTF);
        LocalDate secondDate = LocalDate.parse(end, YYYY_MM_DD_DTF);

        // 计算两个日期之间的年数差异
        return ChronoUnit.DAYS.between(startDate, secondDate);
    }

    /**
     * 将 java.util.Date 转换成 java.time.LocalDate
     * 使用系统默认时区
     *
     * @param date 要转换的 java.util.Date
     * @return 转换后的 java.time.LocalDate
     */
    public static LocalDate convertDateToLocalDate(Date date) {
        // 将 Date 转换为 Instant
        Instant instant = date.toInstant();
        // 使用系统默认时区将 Instant 转换为 ZonedDateTime
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        // 从 ZonedDateTime 提取 LocalDate
        return zdt.toLocalDate();
    }
}
