package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class AdvertRepresentationExtended extends AdvertRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple resource;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private List<ResourceConditionRepresentation> conditions;

    private PrismOpportunityType opportunityType;

    private String name;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }

    public ResourceRepresentationSimple getInstitution() {
        return institution;
    }

    public void setInstitution(ResourceRepresentationSimple institution) {
        this.institution = institution;
    }

    public ResourceRepresentationSimple getDepartment() {
        return department;
    }

    public void setDepartment(ResourceRepresentationSimple department) {
        this.department = department;
    }

    public List<ResourceConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionRepresentation> conditions) {
        this.conditions = conditions;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
