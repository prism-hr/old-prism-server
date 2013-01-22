package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class RefereesAdminEditDTO {

    private String editedRefereeId;

    @ESAPIConstraint(rule = "ExtendedAscii", maxLength = 500)
    private String comment;

    private Document referenceDocument; 

    private Boolean suitableForUCL;

    private Boolean suitableForProgramme;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isSuitableForUCLSet() {
        return suitableForUCL != null;
    }

    public boolean isSuitableForProgrammeSet() {
        return suitableForProgramme != null;
    }

    public Boolean getSuitableForUCL() {
        return suitableForUCL;
    }

    public void setSuitableForUCL(Boolean suitableForUCL) {
        this.suitableForUCL = suitableForUCL;
    }

    public Boolean getSuitableForProgramme() {
        return suitableForProgramme;
    }

    public void setSuitableForProgramme(Boolean suitableForProgramme) {
        this.suitableForProgramme = suitableForProgramme;
    }

    public String getEditedRefereeId() {
        return editedRefereeId;
    }

    public void setEditedRefereeId(String editedRefereeId) {
        this.editedRefereeId = editedRefereeId;
    }

    public Document getReferenceDocument() {
        return referenceDocument;
    }

    public void setReferenceDocument(Document referenceDocument) {
        this.referenceDocument = referenceDocument;
    }

}