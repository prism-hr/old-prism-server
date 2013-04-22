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
import com.zuehlke.pgadmissions.dao.StageDurationDAO;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.SuggestedSupervisor;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

@Service
public class ConfigurationService {

    private final StageDurationDAO stageDurationDAO;
    
    private final ReminderIntervalDAO reminderIntervalDAO;
    
    private final PersonDAO personDAO;

    public ConfigurationService() {
        this(null, null, null);
    }

    @Autowired
    public ConfigurationService(StageDurationDAO stageDurationDAO, ReminderIntervalDAO reminderIntervalDAO, PersonDAO personDAO) {
        this.stageDurationDAO = stageDurationDAO;
        this.reminderIntervalDAO = reminderIntervalDAO;
        this.personDAO = personDAO;
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
    public void saveConfigurations(List<StageDuration> stageDurations, List<Person> registryContacts,
            ReminderInterval reminderInterval) {

        List<Person> allRegistryUsers = getAllRegistryUsers();
        for (Person person : allRegistryUsers) {
            if (!listContainsId(person, registryContacts)) {
                personDAO.delete(person);
            }
        }
        for (Person person : registryContacts) {
            personDAO.save(person);
        }
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

    private boolean listContainsId(Person person, List<Person> persons) {
        for (Person entry : persons) {
            if (entry.getId().equals(person.getId())) {
                return true;
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
