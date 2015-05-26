package com.zuehlke.pgadmissions.services.helpers.persisters;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.services.ProgramService;

@Component
public class ProgramBackgroundPersister implements ImageDocumentPersister {

    @Inject
    private ProgramService programService;

    @Override
    public void persist(Integer programId, Document image) {
        Program program = programService.getById(programId);
        program.setBackgroundImage(image);
    }

}
