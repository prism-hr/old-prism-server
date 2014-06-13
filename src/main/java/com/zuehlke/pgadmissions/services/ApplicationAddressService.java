package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;

@Service
@Transactional
public class ApplicationAddressService {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    public ApplicationAddress getOrCreate(Application application) {
        ApplicationAddress applicationFormAddress = application.getAddress();
        if (applicationFormAddress == null) {
            applicationFormAddress = new ApplicationAddress();
        }
        return applicationFormAddress;
    }
    
	public void saveOrUpdate(int applicationId, ApplicationAddress applicationFormAddress) {
	    Application application = applicationService.getById(applicationId);
	    ApplicationAddress persistentApplicationFormAddress = application.getAddress();
        if (persistentApplicationFormAddress == null) {
            persistentApplicationFormAddress = new ApplicationAddress();         
            persistentApplicationFormAddress.setApplication(application);
            application.setAddress(persistentApplicationFormAddress);
        }
        applicationCopyHelper.copyApplicationFormAddress(persistentApplicationFormAddress, applicationFormAddress, false);
	}
	
}
