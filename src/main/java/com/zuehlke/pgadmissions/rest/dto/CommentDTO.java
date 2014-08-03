package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.ApplicationResidenceStatus;
import com.zuehlke.pgadmissions.domain.definitions.YesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;

public class CommentDTO {

    private PrismAction action;

    @NotNull
    private String content;

    private PrismState transitionState;

    private Set<CommentAssignedUserDTO> assignedUsers;

    private List<Integer> documents;

    private YesNoUnsureResponse qualified;

    private YesNoUnsureResponse competentInWorkLanguage;

    private ApplicationResidenceStatus residenceStatus;

    private Boolean declinedResponse;

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PrismState getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
    }

    public Set<CommentAssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<CommentAssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<Integer> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Integer> documents) {
        this.documents = documents;
    }

    public YesNoUnsureResponse getQualified() {
        return qualified;
    }

    public void setQualified(YesNoUnsureResponse qualified) {
        this.qualified = qualified;
    }

    public YesNoUnsureResponse getCompetentInWorkLanguage() {
        return competentInWorkLanguage;
    }

    public void setCompetentInWorkLanguage(YesNoUnsureResponse competentInWorkLanguage) {
        this.competentInWorkLanguage = competentInWorkLanguage;
    }

    public ApplicationResidenceStatus getResidenceStatus() {
        return residenceStatus;
    }

    public void setResidenceStatus(ApplicationResidenceStatus residenceStatus) {
        this.residenceStatus = residenceStatus;
    }

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }
}
