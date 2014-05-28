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
    private EntityService entityService;
    
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
    
	public void saveOrUpdate(Application application, ApplicationDocument applicationFormDocument) {
	    ApplicationDocument persistentApplicationFormDocument = application.getApplicationDocument();
        if (persistentApplicationFormDocument == null) {
            persistentApplicationFormDocument = new ApplicationDocument();
            persistentApplicationFormDocument.setApplication(application);
            application.setApplicationDocument(persistentApplicationFormDocument);
            entityService.save(application);
        }
        applicationCopyHelper.copyApplicationFormDocument(persistentApplicationFormDocument, applicationFormDocument, false);
        applicationService.saveOrUpdateApplicationSection(application);
	}
	
}
