package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "STAGE_DURATION")
public class StageDuration {
	
	@Id
	@Enumerated(EnumType.STRING)
	private ApplicationFormStatus stage;
	
	private Integer duration;

	@Enumerated(EnumType.STRING)
	private DurationUnitEnum unit;
	
	public ApplicationFormStatus getStage() {
		return stage;
	}
	
	public void setStage(ApplicationFormStatus status) {
		this.stage = status;
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
        switch (unit) {
        case DAYS:
            return (int) TimeUnit.MINUTES.convert(this.duration, TimeUnit.DAYS);
        case HOURS:
            return (int) TimeUnit.MINUTES.convert(this.duration, TimeUnit.HOURS);
        case WEEKS:
            int weekInDays = this.duration * 7;
            return (int) TimeUnit.MINUTES.convert(weekInDays, TimeUnit.DAYS);
        case MINUTES:
            return this.duration;
        default:
            throw new IllegalArgumentException();
        }
	}	
}
