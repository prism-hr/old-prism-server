package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;

@Service
@Transactional
public class ApplicationFormDocumentService {

    @Autowired
    private ApplicationService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

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
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyApplicationFormDocument(persistentApplicationFormDocument, applicationFormDocument, false);
        applicationFormService.saveOrUpdateApplicationSection(application);
	}
	
}
