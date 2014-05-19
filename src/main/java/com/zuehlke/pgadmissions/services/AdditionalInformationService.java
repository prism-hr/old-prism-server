package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
@Transactional
public class AdditionalInformationService {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    ApplicationFormCopyHelper applicationFormCopyHelper;

    public AdditionalInformation getOrCreate(ApplicationForm application) {
        AdditionalInformation additionalInformation = application.getAdditionalInformation();
        if (additionalInformation == null) {
            additionalInformation = new AdditionalInformation();
        }
        return additionalInformation;
    }
    
	public void saveOrUpdate(ApplicationForm application, AdditionalInformation additionalInformation) {
	    AdditionalInformation persistentAdditionalInformation = application.getAdditionalInformation();
	    if (persistentAdditionalInformation == null) {
	        persistentAdditionalInformation = new AdditionalInformation();
	        persistentAdditionalInformation.setApplication(application);
	        application.setAdditionalInformation(persistentAdditionalInformation);
	        applicationFormService.save(application);
	    }
	    applicationFormCopyHelper.copyAdditionalInformation(persistentAdditionalInformation, additionalInformation);
	    applicationFormService.saveOrUpdateApplicationSection(application);
	}
	
}
