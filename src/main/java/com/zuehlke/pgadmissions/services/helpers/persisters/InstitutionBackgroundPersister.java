package com.zuehlke.pgadmissions.services.helpers.persisters;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.services.InstitutionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

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
