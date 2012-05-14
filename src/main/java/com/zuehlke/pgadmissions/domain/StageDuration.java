package com.zuehlke.pgadmissions.domain;

import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StageDuration {

	
	private ApplicationFormStatus status;
	private int durationInDays;
	
	public ApplicationFormStatus getStatus() {
		return status;
	}
	public void setStatus(ApplicationFormStatus status) {
		this.status = status;
	}
	public int getDurationInDays() {
		return durationInDays;
	}
	public void setDurationInDays(int durationInDays) {
		this.durationInDays = durationInDays;
	}
	
}
