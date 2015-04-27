package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;

public class ActionDTO {

    @NotNull
    private PrismAction actionId;

    private Integer workflowPropertyConfigurationVersion;

    @Valid
    private ResourceDTO newResource;

    private String referer;

    public PrismAction getActionId() {
        return actionId;
    }

    public void setActionId(PrismAction actionId) {
        this.actionId = actionId;
    }

    public Integer getWorkflowPropertyConfigurationVersion() {
        return workflowPropertyConfigurationVersion;
    }

    public void setWorkflowPropertyConfigurationVersion(Integer workflowPropertyConfigurationVersion) {
        this.workflowPropertyConfigurationVersion = workflowPropertyConfigurationVersion;
    }

    public ResourceDTO getNewResource() {
        return newResource;
    }

    public void setNewResource(ResourceDTO newResource) {
        this.newResource = newResource;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public ActionDTO withActionId(PrismAction actionId) {
        this.actionId = actionId;
        return this;
    }

}
