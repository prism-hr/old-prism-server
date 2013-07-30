package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class ReminderIntervalBuilder {

    private Integer id;

    private ReminderType reminderType;

    private Integer duration;

    private DurationUnitEnum unit;

    public ReminderIntervalBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ReminderIntervalBuilder reminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
        return this;
    }

    public ReminderIntervalBuilder duration(Integer duration) {
        this.duration = duration;
        return this;
    }

    public ReminderIntervalBuilder unit(DurationUnitEnum unit) {
        this.unit = unit;
        return this;
    }

    public ReminderInterval build() {
        ReminderInterval reminderInterval = new ReminderInterval();
        reminderInterval.setId(id);
        reminderInterval.setReminderType(reminderType);
        reminderInterval.setDuration(duration);
        reminderInterval.setUnit(unit);
        return reminderInterval;
    }
}
