package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class StageDurationBuilder {
	
	private ApplicationFormStatus stage;
	private Integer duration;
	private DurationUnitEnum unit;

	public StageDurationBuilder stage(ApplicationFormStatus stage) {
		this.stage = stage;
		return this;
	}
	
	public StageDurationBuilder duration(Integer duration) {
		this.duration = duration;
		return this;
	}
	
	public StageDurationBuilder unit(DurationUnitEnum unit) {
		this.unit = unit;
		return this;
	}
	
	public StageDuration build() {
		StageDuration stageDuration = new StageDuration();
		stageDuration.setDuration(duration);
		stageDuration.setStage(stage);
		stageDuration.setUnit(unit);
		return stageDuration;
	}
}
