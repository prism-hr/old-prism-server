package com.zuehlke.pgadmissions.rest.representation.advert;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceConditionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class AdvertRepresentationExtended extends AdvertRepresentationSimple {

    private UserRepresentationSimple user;

    private ResourceRepresentationSimple institution;

    private ResourceRepresentationSimple department;

    private ResourceRepresentationSimple program;

    private ResourceRepresentationSimple resource;

    private List<ResourceConditionRepresentation> conditions;

    private List<AdvertCompetenceRepresentation> competences;

    private PrismOpportunityType opportunityType;

    private List<PrismStudyOption> studyOptions;

    private String name;

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
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

    public ResourceRepresentationSimple getProgram() {
        return program;
    }

    public void setProgram(ResourceRepresentationSimple program) {
        this.program = program;
    }

    public ResourceRepresentationSimple getResource() {
        return resource;
    }

    public void setResource(ResourceRepresentationSimple resource) {
        this.resource = resource;
    }

    public List<ResourceConditionRepresentation> getConditions() {
        return conditions;
    }

    public void setConditions(List<ResourceConditionRepresentation> conditions) {
        this.conditions = conditions;
    }

    public List<AdvertCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<AdvertCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
