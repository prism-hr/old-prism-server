package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class StageDurationDAOTest {

	
	@Test
	public void shouldReturnSevenDaysForValidation(){
		assertEquals(7, ((StageDuration)new StageDurationDAO().getByStatus(ApplicationFormStatus.VALIDATION)).getDurationInDays());
	}
	

	@Test
	public void shouldReturn14DaysForReview(){
		assertEquals(14, ((StageDuration)new StageDurationDAO().getByStatus(ApplicationFormStatus.REVIEW)).getDurationInDays());
	}
}
