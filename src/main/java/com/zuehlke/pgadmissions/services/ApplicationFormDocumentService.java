package com.zuehlke.pgadmissions.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.components.ApplicationFormCopyHelper;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;

@Service
@Transactional
public class ApplicationFormDocumentService {

    @Autowired
    private ApplicationFormService applicationFormService;
    
    @Autowired
    private ApplicationFormCopyHelper applicationFormCopyHelper;

    public ApplicationFormDocument getOrCreate(ApplicationForm application) {
        ApplicationFormDocument applicationFormDocument = application.getApplicationFormDocument();
        if (applicationFormDocument  == null) {
            applicationFormDocument = new ApplicationFormDocument();
        }
        return applicationFormDocument;
    }
    
	public void saveOrUpdate(ApplicationForm application, ApplicationFormDocument applicationFormDocument) {
	    ApplicationFormDocument persistentApplicationFormDocument = application.getApplicationFormDocument();
        if (persistentApplicationFormDocument == null) {
            persistentApplicationFormDocument = new ApplicationFormDocument();
            persistentApplicationFormDocument.setApplication(application);
            application.setApplicationFormDocument(persistentApplicationFormDocument);
            applicationFormService.save(application);
        }
        applicationFormCopyHelper.copyApplicationFormDocument(persistentApplicationFormDocument, applicationFormDocument, false);
        applicationFormService.saveOrUpdateApplicationFormSection(application);
	}
	
}
