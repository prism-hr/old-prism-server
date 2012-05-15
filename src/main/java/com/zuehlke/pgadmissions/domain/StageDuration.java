package com.zuehlke.pgadmissions.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;


import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

@Entity(name = "STAGE_DURATION")
@Access(AccessType.FIELD)
public class StageDuration{
	
	private static final int LARGE_PRIME = 3257;
	
	@Id
	@Access(AccessType.PROPERTY)
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
	
	
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (stage == null) {
			return false;
		}
		if (((StageDuration) other).getStage() == null) {
			return false;
		}
		if (!this.getClass().equals(other.getClass())) {
			return false;
		}
		return stage.equals(((StageDuration) other).getStage());
	}

	@Override
	public int hashCode() {
		if(stage == null){
			return LARGE_PRIME;
		}
		return LARGE_PRIME * stage.hashCode();
	}
}
