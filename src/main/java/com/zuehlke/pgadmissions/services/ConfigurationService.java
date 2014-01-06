package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.NotificationsDurationDAO;
import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.NotificationsDuration;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;

@Service
public class ConfigurationService {

    private final StageDurationDAO stageDurationDAO;
    
    private final ReminderIntervalDAO reminderIntervalDAO;

    private final NotificationsDurationDAO notificationsDurationDAO;
    
    private final PersonDAO personDAO;
    
    private final UserDAO userDAO;
    
    private final ApplicationFormUserRoleService applicationFormUserRoleService;    

    public ConfigurationService() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ConfigurationService(final StageDurationDAO stageDurationDAO,
            final ReminderIntervalDAO reminderIntervalDAO, final NotificationsDurationDAO notificationsDurationDAO,
            final PersonDAO personDAO, final UserDAO userDAO, final ApplicationFormUserRoleService applicationFormUserRoleService) {
        this.stageDurationDAO = stageDurationDAO;
        this.reminderIntervalDAO = reminderIntervalDAO;
        this.notificationsDurationDAO = notificationsDurationDAO;
        this.personDAO = personDAO;
        this.userDAO  = userDAO;
        this.applicationFormUserRoleService = applicationFormUserRoleService;
    }

    @Transactional
    public Person getRegistryUserWithId(Integer id) {
        return personDAO.getPersonWithId(id);
    }

    @Transactional
    public List<Person> getAllRegistryUsers() {
        List<Person> allPersons = personDAO.getAllPersons();
        List<Person> allRegistryUsers = new ArrayList<Person>();
        for (Person person : allPersons) {
            if (!(person instanceof SuggestedSupervisor)) {
                allRegistryUsers.add(person);
            }
        }
        return allRegistryUsers;
    }

    @Transactional
    public void saveConfigurations(ServiceLevelsDTO serviceLevelsDTO) {
        for (StageDuration stageDuration : serviceLevelsDTO.getStagesDuration()) {
            StageDuration oldDuration = stageDurationDAO.getByStatus(stageDuration.getStage());
            if (oldDuration != null) {
                oldDuration.setUnit(stageDuration.getUnit());
                oldDuration.setDuration(stageDuration.getDuration());
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
    public void saveRegistryUsers(List<Person> registryContacts, RegisteredUser requestedBy) {
        for (Person person : getAllRegistryUsers()) {
            if (!containsRegistryUser(person, registryContacts)) {
                personDAO.delete(person);
                removeAdmitterRoleToUser(person.getEmail());
            }
        }
        
        for (Person person : registryContacts) {
            personDAO.save(person);
        }
        
        for (Person person : registryContacts) {
        	RegisteredUser userToAssign = applicationFormUserRoleService.createRegisteredUser(person.getFirstname(), person.getLastname(), person.getEmail());
        	applicationFormUserRoleService.grantUserSystemRoles(userToAssign, Authority.ADMITTER);
        }
    }
    
    private void removeAdmitterRoleToUser(String email) {
        RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(email);
        if (user != null) {
            applicationFormUserRoleService.revokeUserFromSystemRoles(user, Authority.ADMITTER);
        }
    }

    @Transactional
    public Map<ApplicationFormStatus, StageDuration> getStageDurations() {
        Map<ApplicationFormStatus, StageDuration> stageDurations = new HashMap<ApplicationFormStatus, StageDuration>();
        ApplicationFormStatus[] configurableStages = getConfigurableStages();
        for (ApplicationFormStatus applicationFormStatus : configurableStages) {
            stageDurations.put(applicationFormStatus, stageDurationDAO.getByStatus(applicationFormStatus));
        }
        return stageDurations;
    }

    @Transactional
    public List<ReminderInterval> getReminderIntervals() {
        return reminderIntervalDAO.getReminderIntervals();
    }
    
    @Transactional
    public NotificationsDuration getNotificationsDuration() {
        return notificationsDurationDAO.getNotificationsDuration();
    }

    private boolean containsRegistryUser(Person person, List<Person> persons) {
        for (Person entry : persons) {
            if (entry.getId()!=null) {
                if (entry.getId().equals(person.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public ApplicationFormStatus[] getConfigurableStages() {
        return new ApplicationFormStatus[] { 
                ApplicationFormStatus.VALIDATION, ApplicationFormStatus.REVIEW,
                ApplicationFormStatus.INTERVIEW, ApplicationFormStatus.APPROVAL };
    }

}