package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Service
@Transactional
public class ApplicationFormDocumentService {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public ApplicationDocument getOrCreate(ApplicationForm application) {
        ApplicationDocument applicationFormDocument = application.getApplicationDocument();
        if (applicationFormDocument  == null) {
            applicationFormDocument = new ApplicationDocument();
        }
        return applicationFormDocument;
    }
    
	public void saveOrUpdate(ApplicationForm application, ApplicationDocument applicationFormDocument) {
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
