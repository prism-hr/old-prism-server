package uk.co.alumeni.prism.services.helpers.persisters;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.services.InstitutionService;

import javax.inject.Inject;

@Component
public class InstitutionBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private InstitutionService institutionService;

    @Override
    public void persist(Integer institutionId, Document image) {
        Institution institution = institutionService.getById(institutionId);
        institution.getAdvert().setBackgroundImage(image);
    }

}
