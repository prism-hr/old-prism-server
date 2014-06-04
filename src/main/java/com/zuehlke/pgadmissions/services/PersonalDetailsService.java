package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationPersonalDetails;
import com.zuehlke.pgadmissions.domain.User;

@Service
@Transactional
public class PersonalDetailsService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCopyHelper applicationFormCopyHelper;
    
    public ApplicationPersonalDetails getOrCreate(Application application) {
        ApplicationPersonalDetails personalDetails = application.getPersonalDetails();
        if (personalDetails == null) {
            personalDetails = new ApplicationPersonalDetails();
        }
        return personalDetails;
    }
    
    public void saveOrUpdate(int applicationId, ApplicationPersonalDetails personalDetails, User updatedUser) {
        Application application = applicationService.getById(applicationId);
        
        ApplicationPersonalDetails persistentPersonalDetails = application.getPersonalDetails();
        if (persistentPersonalDetails == null) {
            persistentPersonalDetails = new ApplicationPersonalDetails();
            persistentPersonalDetails.setApplication(application);
            application.setPersonalDetails(persistentPersonalDetails);
        }
        updateApplicantData(application, updatedUser);
        applicationFormCopyHelper.copyPersonalDetails(persistentPersonalDetails, personalDetails, false);
    }
     
    private void updateApplicantData(Application application, User updatedUser) {
        User persistentUpdatedUser = application.getUser();
        persistentUpdatedUser.setFirstName(updatedUser.getFirstName());
        persistentUpdatedUser.setFirstName2(updatedUser.getFirstName2());
        persistentUpdatedUser.setFirstName3(updatedUser.getFirstName3());
        persistentUpdatedUser.setLastName(updatedUser.getLastName());
    }

}
