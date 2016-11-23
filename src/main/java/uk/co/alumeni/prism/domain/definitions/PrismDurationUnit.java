package uk.co.alumeni.prism.domain.definitions;

import static uk.co.alumeni.prism.PrismConstants.*;

public enum PrismDurationUnit {

    HOUR, //
    DAY, //
    WEEK, //
    MONTH, //
    YEAR;

    public static Integer getDurationUnitAsHours(PrismDurationUnit interval) {
        Integer value = 1;

        if (interval.equals(YEAR)) {
            value = value * WORK_MONTHS_IN_YEAR;
        }

        int intervalOrdinal = interval.ordinal();

        if (intervalOrdinal > WEEK.ordinal()) {
            value = value * WORK_WEEKS_IN_MONTH;
        }

        if (intervalOrdinal > DAY.ordinal()) {
            value = value * WORK_DAYS_IN_WEEK;
        }

        if (intervalOrdinal > HOUR.ordinal()) {
            value = value * WORK_HOURS_IN_DAY;
        }

        return value;
    }

}
