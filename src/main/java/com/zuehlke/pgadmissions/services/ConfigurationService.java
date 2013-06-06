package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PersonDAO;
import com.zuehlke.pgadmissions.dao.ReminderIntervalDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.PendingRoleNotification;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Service
public class ConfigurationService {

    private final StageDurationDAO stageDurationDAO;
    
    private final ReminderIntervalDAO reminderIntervalDAO;
    
    private final PersonDAO personDAO;
    
    private final UserDAO userDAO;
    
    private final RoleDAO roleDAO;
    
    private final UserFactory userFactory;
    

    public ConfigurationService() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public ConfigurationService(final StageDurationDAO stageDurationDAO,
            final ReminderIntervalDAO reminderIntervalDAO,
            final PersonDAO personDAO, final UserDAO userDAO,
            final UserFactory userFactory, final RoleDAO roleDAO) {
        this.stageDurationDAO = stageDurationDAO;
        this.reminderIntervalDAO = reminderIntervalDAO;
        this.personDAO = personDAO;
        this.userDAO  = userDAO;
        this.userFactory = userFactory;
        this.roleDAO = roleDAO;
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
    public void saveConfigurations(List<StageDuration> stageDurations, ReminderInterval reminderInterval) {
        
        for (StageDuration stageDuration : stageDurations) {
            StageDuration oldDuration = stageDurationDAO.getByStatus(stageDuration.getStage());
            if (oldDuration != null) {
                oldDuration.setUnit(stageDuration.getUnit());
                oldDuration.setDuration(stageDuration.getDuration());
                stageDurationDAO.save(oldDuration);
            } else {
                stageDurationDAO.save(stageDuration);
            }
        }
        
        reminderIntervalDAO.save(reminderInterval);
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
            saveRegistryContactsAsUsers(person, requestedBy);
        }
    }
    
    private void removeAdmitterRoleToUser(String email) {
        RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(email);
        if (user!=null) {
            user.removeRole(Authority.ADMITTER);
            userDAO.save(user);
        }
    }

    private void saveRegistryContactsAsUsers(final Person registryContact, RegisteredUser requestedBy) {
        RegisteredUser user = userDAO.getUserByEmailIncludingDisabledAccounts(registryContact.getEmail());
        PendingRoleNotification admitterNotification = new PendingRoleNotification();
        admitterNotification.setAddedByUser(requestedBy);
        admitterNotification.setRole(roleDAO.getRoleByAuthority(Authority.ADMITTER));
        PendingRoleNotification viewerNotification = new PendingRoleNotification();
        viewerNotification.setAddedByUser(requestedBy);
        viewerNotification.setRole(roleDAO.getRoleByAuthority(Authority.VIEWER));
        if (user == null) {
            user = userFactory.createNewUserInRoles(registryContact.getFirstname(), registryContact.getLastname(), registryContact.getEmail(), Authority.VIEWER, Authority.ADMITTER);
            user.getPendingRoleNotifications().add(viewerNotification);
            user.getPendingRoleNotifications().add(admitterNotification);
            userDAO.save(user);
        } else if (user != null && user.isNotInRole(Authority.ADMITTER)) {
            user.getRoles().add(roleDAO.getRoleByAuthority(Authority.ADMITTER));
            user.getPendingRoleNotifications().add(admitterNotification);
            userDAO.save(user);
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
    public ReminderInterval getReminderInterval() {
        return reminderIntervalDAO.getReminderInterval();
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
