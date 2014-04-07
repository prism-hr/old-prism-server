package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormAddress;

@Service
@Transactional
public class ApplicationFormAddressService {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public ApplicationFormAddress getOrCreate(ApplicationForm application) {
        ApplicationFormAddress applicationFormAddress = application.getApplicationFormAddress();
        if (applicationFormAddress == null) {
            applicationFormAddress = new ApplicationFormAddress();
        }
        return applicationFormAddress;
    }
    
	public void saveOrUpdate(ApplicationForm application, ApplicationFormAddress applicationFormAddress) {
	    ApplicationFormAddress persistentApplicationFormAddress = application.getApplicationFormAddress();
        if (persistentApplicationFormAddress == null) {
            persistentApplicationFormAddress = new ApplicationFormAddress();         
            persistentApplicationFormAddress.setApplication(application);
            application.setApplicationFormAddress(persistentApplicationFormAddress);
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyApplicationFormAddress(persistentApplicationFormAddress, applicationFormAddress, false);
        applicationFormService.saveOrUpdateApplicationSection(application);
	}
	
}
