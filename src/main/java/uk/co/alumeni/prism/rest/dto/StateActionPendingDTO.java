package uk.co.alumeni.prism.rest.dto;

import java.util.List;

import javax.validation.Valid;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.rest.dto.user.UserDTO;

public class StateActionPendingDTO {

    private String content;

    private PrismRole assignUserRole;

    @Valid
    private List<UserDTO> assignUserList;

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

    public List<UserDTO> getAssignUserList() {
        return assignUserList;
    }

    public void setAssignUserList(List<UserDTO> assignUserList) {
        this.assignUserList = assignUserList;
    }

    public String getAssignUserMessage() {
        return assignUserMessage;
    }

    public void setAssignUserMessage(String assignUserMessage) {
        this.assignUserMessage = assignUserMessage;
    }

}
