package uk.co.alumeni.prism.services.helpers.persisters;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.helpers.processors.InstitutionEmailLogoProcessor;

@Component
public class InstitutionLogoPersister implements ImageDocumentPersister {

    @Inject
    private InstitutionService institutionService;

    @Inject
    private InstitutionEmailLogoProcessor institutionEmailLogoProcessor;

    @Inject
    private EntityService entityService;

    @Override
    public void persist(Integer institutionId, Document image) {
        Institution institution = institutionService.getById(institutionId);
        institution.setLogoImage(image);

        byte[] emailLogoContent = institutionEmailLogoProcessor.process(image.getContent(), image.getContentType());
        Document imageEmail = new Document().withContent(emailLogoContent).withContentType(image.getContentType())
                .withExported(false).withFileName("email_" + image.getFileName())
                .withUser(image.getUser()).withCreatedTimestamp(new DateTime()).withCategory(image.getCategory());
        entityService.save(imageEmail);
        institution.setLogoImageEmail(imageEmail);
    }

}
