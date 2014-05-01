package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dao.NotificationsDurationDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.NotificationsDurationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReminderIntervalBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.ReminderType;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ConfigurationServiceTest {

    @Mock
    @InjectIntoByType
    private StateDAO stateDAOMock;

    @Mock
    @InjectIntoByType
    private ReminderIntervalDAO reminderIntervalDAOMock;

    @Mock
    @InjectIntoByType
    private NotificationsDurationDAO notificationsDurationDAOMock;

    @Mock
    @InjectIntoByType
    private UserDAO userDAOMock;

    @Mock
    @InjectIntoByType
    private WorkflowService applicationFormUserRoleService;

    @Mock
    @InjectIntoByType
    private RoleService roleServiceMock;

    @Mock
    @InjectIntoByType
    private RoleDAO roleDAOMock;

    @TestedObject
    private ConfigurationService service;

    @Test
    public void shouldSaveConfigurationObjects() {

        State validationDuration = null;// new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(1).unit(DurationUnitEnum.DAYS).build();
        State oldValidationDuration = null; // new StageDurationBuilder().stage(ApplicationFormStatus.VALIDATION).duration(5).unit(DurationUnitEnum.WEEKS)
                                            // .build();

        ReminderInterval reminderInterval = new ReminderIntervalBuilder().reminderType(ReminderType.INTERVIEW_SCHEDULE).duration(8)
                .unit(DurationUnitEnum.WEEKS).build();
        ReminderInterval oldReminderInterval = new ReminderIntervalBuilder().reminderType(ReminderType.INTERVIEW_SCHEDULE).duration(10)
                .unit(DurationUnitEnum.DAYS).build();

        NotificationsDuration notificationsDuration = new NotificationsDurationBuilder().duration(8).unit(DurationUnitEnum.WEEKS).build();
        NotificationsDuration oldNotificationsDuration = new NotificationsDurationBuilder().id(1).duration(1).unit(DurationUnitEnum.DAYS).build();

        ServiceLevelsDTO serviceLevelsDTO = new ServiceLevelsDTO();
        serviceLevelsDTO.setStagesDuration(Lists.newArrayList(validationDuration));
        serviceLevelsDTO.setReminderIntervals(Lists.newArrayList(reminderInterval));
        serviceLevelsDTO.setNotificationsDuration(notificationsDuration);

        EasyMock.expect(stateDAOMock.getById(ApplicationFormStatus.APPLICATION_VALIDATION)).andReturn(oldValidationDuration);
        EasyMock.expect(reminderIntervalDAOMock.getReminderInterval(ReminderType.INTERVIEW_SCHEDULE)).andReturn(oldReminderInterval);
        EasyMock.expect(notificationsDurationDAOMock.getNotificationsDuration()).andReturn(oldNotificationsDuration);

        EasyMock.replay(stateDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock);
        service.saveConfigurations(serviceLevelsDTO);
        EasyMock.verify(stateDAOMock, reminderIntervalDAOMock, notificationsDurationDAOMock);

        assertEquals(DurationUnitEnum.WEEKS, oldReminderInterval.getUnit());
        assertEquals(DurationUnitEnum.WEEKS, oldNotificationsDuration.getUnit());
    }

    @Test
    public void shouldGetReminderInterval() {
        ArrayList<ReminderInterval> intervals = Lists.newArrayList();

        EasyMock.expect(reminderIntervalDAOMock.getReminderIntervals()).andReturn(intervals);
        EasyMock.replay(reminderIntervalDAOMock);
        assertSame(intervals, service.getReminderIntervals());
    }

}
