package com.zuehlke.pgadmissions.domain;

import java.util.concurrent.TimeUnit;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "REMINDER_INTERVAL")
@Access(AccessType.FIELD)
public class ReminderInterval{
	
	private static final int LARGE_PRIME = 3257;
	
	@Id
	@Access(AccessType.PROPERTY)
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
	
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (id == null) {
			return false;
		}
		if (((ReminderInterval) other).getId() == null) {
			return false;
		}
		if (!this.getClass().equals(other.getClass())) {
			return false;
		}
		return id.equals(((ReminderInterval) other).getId());
	}

	@Override
	public int hashCode() {
		if(id == null){
			return LARGE_PRIME;
		}
		return LARGE_PRIME * id.hashCode();
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
