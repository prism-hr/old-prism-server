package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.Application;

@Service
@Transactional
public class AdditionalInformationService {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    ApplicationCopyHelper applicationFormCopyHelper;

    public ApplicationAdditionalInformation getOrCreate(Application application) {
        ApplicationAdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new ApplicationAdditionalInformation();
        }
        return additionalInformation;
    }
    
	public void saveOrUpdate(int applicationId, ApplicationAdditionalInformation additionalInformation) {
	    Application application = applicationService.getById(applicationId);
	    ApplicationAdditionalInformation persistentAdditionalInformation = application.getAdditionalInformation();
	    if (persistentAdditionalInformation == null) {
	        persistentAdditionalInformation = new ApplicationAdditionalInformation();
	        persistentAdditionalInformation.setApplication(application);
	        application.setAdditionalInformation(persistentAdditionalInformation);
	    }
	    applicationFormCopyHelper.copyAdditionalInformation(persistentAdditionalInformation, additionalInformation);
	}
	
}
