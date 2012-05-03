package com.zuehlke.pgadmissions.dao;

import org.springframework.stereotype.Repository;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Repository
public class StageDurationDAO {

	public StageDuration getByStatus(ApplicationFormStatus status) {
		StageDuration stageDuration = new StageDuration();
		stageDuration.setStatus(status);
		if( status == ApplicationFormStatus.VALIDATION){
			stageDuration.setDurationInDays(7);
		}
		if( status == ApplicationFormStatus.REVIEW){
			stageDuration.setDurationInDays(14);
		}
		return stageDuration;
	}

}
