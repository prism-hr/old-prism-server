package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.referencedata.jaxb.ProgrammeOccurrences.ProgrammeOccurrence.Programme;

public class ProgramImportDTO {

    private Programme programme;

    public final Programme getProgramme() {
        return programme;
    }

    public final void setProgramme(Programme programme) {
        this.programme = programme;
    }
    
}
