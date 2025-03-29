package com.kita.organizer.data.db;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Since Room doesn't support LocalDate and LocalTime natively, we need TypeConverters.
 */
public class Converters {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @TypeConverter
    public static LocalDate fromStringToDate(String value) {
        return value == null ? null : LocalDate.parse(value, DATE_FORMATTER);
    }

    @TypeConverter
    public static String fromDateToString(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    @TypeConverter
    public static LocalTime fromStringToTime(String value) {
        return value == null ? null : LocalTime.parse(value, TIME_FORMATTER);
    }

    @TypeConverter
    public static String fromTimeToString(LocalTime time) {
        return time == null ? null : time.format(TIME_FORMATTER);
    }
}