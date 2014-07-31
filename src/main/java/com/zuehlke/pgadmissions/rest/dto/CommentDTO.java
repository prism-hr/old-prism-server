package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.ApplicationResidenceStatus;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CommentDTO {

    @NotNull
    private String content;

    private List<Integer> documents;

    private YesNoUnsureResponse qualified;

    private YesNoUnsureResponse competentInWorkLanguage;

    private ApplicationResidenceStatus residenceStatus;

    private Boolean declinedResponse;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Integer> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Integer> documents) {
        this.documents = documents;
    }

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }
}
