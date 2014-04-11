package com.zuehlke.pgadmissions.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationsDurationDAO;
import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StateDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@Service
public class ConfigurationService {

    @Autowired
    private StateDAO stateDAO;

    @Autowired
    private ReminderIntervalDAO reminderIntervalDAO;

    @Autowired
    private NotificationsDurationDAO notificationsDurationDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private WorkflowService applicationFormUserRoleService;

    @Transactional
    public void saveConfigurations(ServiceLevelsDTO serviceLevelsDTO) {
        for (State state : serviceLevelsDTO.getStagesDuration()) {
            State oldState = stateDAO.getById(state.getId());
            if (oldState != null) {
                oldState.setDuration(state.getDuration());
            }
        }

        for (ReminderInterval reminderInterval : serviceLevelsDTO.getReminderIntervals()) {
            ReminderInterval oldReminderInterval = reminderIntervalDAO.getReminderInterval(reminderInterval.getReminderType());
            if (oldReminderInterval != null) {
                oldReminderInterval.setDuration(reminderInterval.getDuration());
                oldReminderInterval.setUnit(reminderInterval.getUnit());
            }
        }

        NotificationsDuration notificationsDuration = serviceLevelsDTO.getNotificationsDuration();
        NotificationsDuration oldNotificationsDuration = notificationsDurationDAO.getNotificationsDuration();
        oldNotificationsDuration.setDuration(notificationsDuration.getDuration());
        oldNotificationsDuration.setUnit(notificationsDuration.getUnit());
    }

    @Transactional
    public List<State> getConfigurableStates() {
        return stateDAO.getAllConfigurableStates();
    }

    @Transactional
    public List<ReminderInterval> getReminderIntervals() {
        return reminderIntervalDAO.getReminderIntervals();
    }

    @Transactional
    public NotificationsDuration getNotificationsDuration() {
        return notificationsDurationDAO.getNotificationsDuration();
    }

}