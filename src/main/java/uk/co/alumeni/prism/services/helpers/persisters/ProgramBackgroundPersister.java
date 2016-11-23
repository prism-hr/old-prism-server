package uk.co.alumeni.prism.services.helpers.persisters;

import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.resource.Program;
import uk.co.alumeni.prism.services.ProgramService;

import javax.inject.Inject;

@Component
public class ProgramBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private ProgramService programService;

    @Override
    public void persist(Integer programId, Document image) {
        Program program = programService.getById(programId);
        program.getAdvert().setBackgroundImage(image);
    }

}
