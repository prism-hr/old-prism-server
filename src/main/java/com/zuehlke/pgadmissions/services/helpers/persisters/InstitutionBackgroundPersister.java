package com.zuehlke.pgadmissions.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.services.InstitutionService;

@Component
public class InstitutionBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private InstitutionService institutionService;

    @Override
    public void persist(Integer institutionId, Document image) {
        Institution institution = institutionService.getById(institutionId);
        institution.setBackgroundImage(image);
    }

}
