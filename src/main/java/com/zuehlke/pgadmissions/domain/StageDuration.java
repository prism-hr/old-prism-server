package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "STAGE_DURATION")
public class StageDuration {
	
	@Id
	@Type(type = "com.zuehlke.pgadmissions.dao.custom.ApplicationFormStatusEnumUserType")
	private ApplicationFormStatus stage;
	
	private Integer duration;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.DurationUnitEnumUserType")
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
	
	public int getDurationInMinutes(){
		if (this.unit == DurationUnitEnum.DAYS){
			return (int)TimeUnit.MINUTES.convert(this.duration, TimeUnit.DAYS);
		}
		if (this.unit == DurationUnitEnum.HOURS){
			return  (int)TimeUnit.MINUTES.convert(this.duration, TimeUnit.HOURS);
		}
		if (this.unit == DurationUnitEnum.WEEKS){
			int weekInDays = this.duration * 7;
			return (int)TimeUnit.MINUTES.convert(weekInDays, TimeUnit.DAYS);
		}
		return this.duration;
	}	
}
