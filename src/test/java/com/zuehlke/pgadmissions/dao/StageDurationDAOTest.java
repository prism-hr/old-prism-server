package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.builders.StageDurationBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class StageDurationDAOTest extends AutomaticRollbackTestCase{
	
	@Test
	public void shouldReturnInterviewStageDuration(){
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(12).unit(DurationUnitEnum.DAYS).build();
		
		sessionFactory.getCurrentSession().saveOrUpdate(stageDuration);
		
		flushAndClearSession();
					
		StageDurationDAO stageDurationDAO = new StageDurationDAO(sessionFactory);
		StageDuration interviewStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.INTERVIEW);
		assertEquals(stageDuration.getStage(), interviewStageDuration.getStage());
		assertEquals(stageDuration.getDuration(), interviewStageDuration.getDuration());
		assertEquals(stageDuration.getUnit(), interviewStageDuration.getUnit());
	}	
	
	@Test
	public void shouldUpdateDurationStageForSameStage(){
		StageDuration stageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(12).unit(DurationUnitEnum.DAYS).build();
		
		sessionFactory.getCurrentSession().saveOrUpdate(stageDuration);
		
		flushAndClearSession();
		
		StageDuration updatedStageDuration = new StageDurationBuilder().stage(ApplicationFormStatus.INTERVIEW).duration(8).unit(DurationUnitEnum.DAYS).build();
		
		sessionFactory.getCurrentSession().saveOrUpdate(updatedStageDuration);
		
		flushAndClearSession();
		
		StageDurationDAO stageDurationDAO = new StageDurationDAO(sessionFactory);
		StageDuration interviewStageDuration = stageDurationDAO.getByStatus(ApplicationFormStatus.INTERVIEW);
		assertEquals(updatedStageDuration.getStage(), interviewStageDuration.getStage());
		assertEquals(updatedStageDuration.getDuration(), interviewStageDuration.getDuration());
		assertEquals(updatedStageDuration.getUnit(), interviewStageDuration.getUnit());
	}	
}