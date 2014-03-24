package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
@Transactional
public class PersonalDetailsService {

    @Autowired
    private ApplicationFormService applicationFormService;

    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;
    
    public PersonalDetails getOrCreate(ApplicationForm application) {
        PersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new PersonalDetails();
        }
        return personalDetails;
    }
    
    public void saveOrUpdate(ApplicationForm application, PersonalDetails personalDetails, RegisteredUser updatedUser) {
        PersonalDetails persistentPersonalDetails = application.getPersonalDetails();
        if (persistentPersonalDetails == null) {
            persistentPersonalDetails = new PersonalDetails();
            persistentPersonalDetails.setApplication(application);
            application.setPersonalDetails(persistentPersonalDetails);
            applicationFormService.save(application);
        }
        updateApplicantData(application, updatedUser);
        applicationFormCopyHelper.copyPersonalDetails(persistentPersonalDetails, personalDetails, false);
        applicationFormService.saveOrUpdateApplicationFormSection(application);
    }
     
    private void updateApplicantData(ApplicationForm application, RegisteredUser updatedUser) {
        RegisteredUser persistentUpdatedUser = application.getApplicant();
        persistentUpdatedUser.setFirstName(updatedUser.getFirstName());
        persistentUpdatedUser.setFirstName2(updatedUser.getFirstName2());
        persistentUpdatedUser.setFirstName3(updatedUser.getFirstName3());
        persistentUpdatedUser.setLastName(updatedUser.getLastName());
    }

}
