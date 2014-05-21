package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Application;

@Service
@Transactional
public class AdditionalInformationService {
    
    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    ApplicationFormCopyHelper applicationFormCopyHelper;

    public AdditionalInformation getOrCreate(Application application) {
        AdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new AdditionalInformation();
        }
        return additionalInformation;
    }
    
	public void saveOrUpdate(Application application, AdditionalInformation additionalInformation) {
	    AdditionalInformation persistentAdditionalInformation = application.getAdditionalInformation();
	    if (persistentAdditionalInformation == null) {
	        persistentAdditionalInformation = new AdditionalInformation();
	        persistentAdditionalInformation.setApplication(application);
	        application.setAdditionalInformation(persistentAdditionalInformation);
	        applicationService.saveUpdate(application);
	    }
	    applicationFormCopyHelper.copyAdditionalInformation(persistentAdditionalInformation, additionalInformation);
	    applicationService.saveOrUpdateApplicationSection(application);
	}
	
}
