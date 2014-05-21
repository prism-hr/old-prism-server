package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;

@Service
@Transactional
public class ApplicationFormAddressService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public ApplicationAddress getOrCreate(Application application) {
        ApplicationAddress applicationFormAddress = application.getApplicationAddress();
        if (applicationFormAddress == null) {
            applicationFormAddress = new ApplicationAddress();
        }
        return applicationFormAddress;
    }
    
	public void saveOrUpdate(Application application, ApplicationAddress applicationFormAddress) {
	    ApplicationAddress persistentApplicationFormAddress = application.getApplicationAddress();
        if (persistentApplicationFormAddress == null) {
            persistentApplicationFormAddress = new ApplicationAddress();         
            persistentApplicationFormAddress.setApplication(application);
            application.setApplicationAddress(persistentApplicationFormAddress);
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyApplicationFormAddress(persistentApplicationFormAddress, applicationFormAddress, false);
        applicationFormService.saveOrUpdateApplicationSection(application);
	}
	
}
