package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;

public class ApplicationDocumentDTO {

    @Valid
    private FileDTO cv;

    @Valid
    private FileDTO coveringLetter;

    public FileDTO getCv() {
        return cv;
    }

    public void setCv(FileDTO cv) {
        this.cv = cv;
    }

    public FileDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(FileDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

}
