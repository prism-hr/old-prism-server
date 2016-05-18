package uk.co.alumeni.prism.utils;

import static org.joda.time.DateTimeConstants.MONDAY;

import org.joda.time.LocalDate;

public class PrismDateUtils {

    public static LocalDate getNextMonday(LocalDate baseline) {
        if (baseline.getDayOfWeek() >= MONDAY) {
            baseline = baseline.plusWeeks(1);
        }
        return baseline.withDayOfWeek(MONDAY);
    }

}
