package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "NOTIFICATIONS_DURATION")
public class NotificationsDuration {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private DurationUnitEnum unit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
