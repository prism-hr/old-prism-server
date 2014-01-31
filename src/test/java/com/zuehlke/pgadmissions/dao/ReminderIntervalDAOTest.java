package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;

public class ReminderIntervalDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldReturnReminderInterval() {
        ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        reminderIntervalDAO.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE);
    }

    @Test
    public void shouldModifyReminderInterval() {

        ReminderIntervalDAO reminderIntervalDAO = new ReminderIntervalDAO(sessionFactory);
        ReminderInterval interval = reminderIntervalDAO.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE);

        interval.setDuration(10);
        interval.setUnit(DurationUnitEnum.DAYS);

        flushAndClearSession();

        ReminderInterval updatedReminderInterval = reminderIntervalDAO.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE);

        assertEquals(updatedReminderInterval.getId(), interval.getId());
        assertEquals(updatedReminderInterval.getDuration(), interval.getDuration());
        assertEquals(updatedReminderInterval.getUnit(), interval.getUnit());
    }
}
