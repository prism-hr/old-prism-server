package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;

public class StateActionPendingDTO {

    private String content;

    private PrismRole assignUserRole;

    private String assignUserList;

    private String assignUserMessage;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PrismRole getAssignUserRole() {
        return assignUserRole;
    }

    public void setAssignUserRole(PrismRole assignUserRole) {
        this.assignUserRole = assignUserRole;
    }

    public String getAssignUserList() {
        return assignUserList;
    }

    public void setAssignUserList(String assignUserList) {
        this.assignUserList = assignUserList;
    }

    public String getAssignUserMessage() {
        return assignUserMessage;
    }

    public void setAssignUserMessage(String assignUserMessage) {
        this.assignUserMessage = assignUserMessage;
    }

}
