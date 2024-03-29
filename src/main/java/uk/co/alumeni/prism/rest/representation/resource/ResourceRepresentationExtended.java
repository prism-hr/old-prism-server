package uk.co.alumeni.prism.rest.representation.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.co.alumeni.prism.rest.representation.action.ActionRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.application.ApplicationRepresentationClient;
import uk.co.alumeni.prism.rest.representation.resource.institution.InstitutionRepresentationClient;

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

    private List<ResourceConditionRepresentation> conditions;

    public List<ActionRepresentationExtended> getActions() {
        return actions;
    }

    public void setActions(List<ActionRepresentationExtended> actions) {
        this.actions = actions;
    }

    public List<ResourceConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionRepresentation> conditions) {
        this.conditions = conditions;
    }

}
