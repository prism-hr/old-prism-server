package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;
import org.junit.Test;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class ReminderIntervalDAOTest extends AutomaticRollbackTestCase{
	
	@Test
	public void shouldReturnReminderInterval(){
		
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		flushAndClearSession();
			
		ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
		ReminderInterval interval = reminderIntervalDAO.getReminderInterval();
		
		assertEquals(reminderInterval, interval);
		assertEquals(reminderInterval.getDuration(), interval.getDuration());
		assertEquals(reminderInterval.getUnit(), interval.getUnit());
	}	
	
	@Test
	public void shouldNotCreateANewReminderInterval(){
		ReminderInterval reminderInterval = new ReminderInterval();
		reminderInterval.setId(1);
		reminderInterval.setDuration(1);
		reminderInterval.setUnit(DurationUnitEnum.WEEKS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(reminderInterval);
		
		flushAndClearSession();
		
		ReminderInterval updatedReminderInterval = new ReminderInterval();
		updatedReminderInterval.setId(1);
		updatedReminderInterval.setDuration(10);
		updatedReminderInterval.setUnit(DurationUnitEnum.DAYS);
		
		sessionFactory.getCurrentSession().saveOrUpdate(updatedReminderInterval);
		
		flushAndClearSession();
		
		ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
		
		ReminderInterval interval = reminderIntervalDAO.getReminderInterval();
		
		assertEquals(updatedReminderInterval, interval);
		assertEquals(updatedReminderInterval.getDuration(), interval.getDuration());
		assertEquals(updatedReminderInterval.getUnit(), interval.getUnit());
	}	
}
