package com.zuehlke.pgadmissions.rest.representation.resource;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class ProjectExtendedRepresentation extends AbstractResourceRepresentation {

    private InstitutionRepresentation institution;

    private ProgramRepresentation program;

    private PrismOpportunityType opportunityType;
    
    private String title;

    private AdvertRepresentation advert;
    
    private PrismStudyOption[] studyOptions;
    
    private ResourceSummaryRepresentation resourceSummary;

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

    public ProgramRepresentation getProgram() {
        return program;
    }

    public void setProgram(ProgramRepresentation program) {
        this.program = program;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }
    
    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

    public final ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public final void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }
    
}
