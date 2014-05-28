package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;

@Service
@Transactional
public class ApplicationDocumentService {

    @Autowired
    private ApplicationService applicationService;
    
    @Autowired
    private ApplicationCopyHelper applicationCopyHelper;

    public ApplicationDocument getOrCreate(Application application) {
        ApplicationDocument applicationFormDocument = application.getApplicationDocument();
        if (applicationFormDocument  == null) {
            applicationFormDocument = new ApplicationDocument();
        }
        return applicationFormDocument;
    }
    
	public void saveOrUpdate(int applicationId, ApplicationDocument applicationFormDocument) {
	    Application application = applicationService.getById(applicationId);
	    ApplicationDocument persistentApplicationFormDocument = application.getApplicationDocument();
        if (persistentApplicationFormDocument == null) {
            persistentApplicationFormDocument = new ApplicationDocument();
            persistentApplicationFormDocument.setApplication(application);
            application.setApplicationDocument(persistentApplicationFormDocument);
        }
        applicationCopyHelper.copyApplicationFormDocument(persistentApplicationFormDocument, applicationFormDocument, false);
	}
	
}
