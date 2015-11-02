package com.zuehlke.pgadmissions.rest.representation.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zuehlke.pgadmissions.rest.representation.action.ActionRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "scope")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationRepresentationClient.class, name = "APPLICATION"),
        @JsonSubTypes.Type(value = ProjectRepresentationClient.class, name = "PROJECT"),
        @JsonSubTypes.Type(value = ProgramRepresentationClient.class, name = "PROGRAM"),
        @JsonSubTypes.Type(value = DepartmentRepresentationClient.class, name = "DEPARTMENT"),
        @JsonSubTypes.Type(value = InstitutionRepresentationClient.class, name = "INSTITUTION")
})
public class ResourceRepresentationExtended extends ResourceRepresentationStandard {

    private List<ActionRepresentationExtended> actions;

    private List<ResourceUserRolesRepresentation> userRoles;

    private List<ResourceConditionRepresentation> conditions;

    public List<ActionRepresentationExtended> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationExtended> actions) {
        this.actions = actions;
    }

    public List<ResourceUserRolesRepresentation> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<ResourceUserRolesRepresentation> userRoles) {
        this.userRoles = userRoles;
    }

    public List<ResourceConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionRepresentation> conditions) {
        this.conditions = conditions;
    }

}
