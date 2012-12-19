package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "REMINDER_INTERVAL")
public class ReminderInterval {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	private Integer duration;

	@Type(type = "com.zuehlke.pgadmissions.dao.custom.DurationUnitEnumUserType")
	private DurationUnitEnum unit;
	
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
	
	public int getDurationInMinutes(){
		if (this.unit == DurationUnitEnum.DAYS){
			return (int)TimeUnit.MINUTES.convert(duration, TimeUnit.DAYS);
		}
		if (this.unit == DurationUnitEnum.HOURS){
			return  (int)TimeUnit.MINUTES.convert(duration, TimeUnit.HOURS);
		}
		if (this.unit == DurationUnitEnum.WEEKS){
			int weekInDays = duration * 7;
			return (int)TimeUnit.MINUTES.convert(weekInDays, TimeUnit.DAYS);
		}
		return (int)this.duration;
	}
}
