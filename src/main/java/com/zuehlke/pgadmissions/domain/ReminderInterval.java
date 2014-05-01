package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

@Entity
@Table(name = "REMINDER_INTERVAL")
public class ReminderInterval {

    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false)
    private ReminderType reminderType;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private DurationUnitEnum unit;

    public ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public DurationUnitEnum getUnit() {
        return unit;
    }

    public void setUnit(DurationUnitEnum unit) {
        this.unit = unit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getDurationInMinutes() {
        if (this.unit == DurationUnitEnum.DAYS) {
            return (int) TimeUnit.MINUTES.convert(duration, TimeUnit.DAYS);
        }
        if (this.unit == DurationUnitEnum.WEEKS) {
            int weekInDays = duration * 7;
            return (int) TimeUnit.MINUTES.convert(weekInDays, TimeUnit.DAYS);
        }
        return (int) this.duration;
    }

    public int getDurationInDays() {
        if (this.unit == DurationUnitEnum.WEEKS) {
            int weekInDays = duration * 7;
            return (int) TimeUnit.DAYS.convert(weekInDays, TimeUnit.DAYS);
        }
        return (int) this.duration;
    }
}
