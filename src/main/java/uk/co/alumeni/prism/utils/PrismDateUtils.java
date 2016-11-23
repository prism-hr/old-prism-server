package uk.co.alumeni.prism.utils;

import org.joda.time.LocalDate;

import static org.joda.time.DateTimeConstants.MONDAY;

public class PrismDateUtils {

    public static LocalDate getNextMonday(LocalDate baseline) {
        if (baseline.getDayOfWeek() >= MONDAY) {
            baseline = baseline.plusWeeks(1);
        }
        return baseline.withDayOfWeek(MONDAY);
    }

}
