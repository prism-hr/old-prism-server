package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;

public class NotificationsDurationDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldModifyNotificationsDuration() {

        NotificationsDurationDAO dao = new NotificationsDurationDAO(sessionFactory);
        NotificationsDuration duration = dao.getNotificationsDuration();

        duration.setDuration(88);
        duration.setUnit(DurationUnitEnum.WEEKS);

        dao.save(duration);

        flushAndClearSession();

        NotificationsDuration newDuration = dao.getNotificationsDuration();

        assertEquals(88, (int) newDuration.getDuration());
        assertEquals(DurationUnitEnum.WEEKS, newDuration.getUnit());
    }
}
