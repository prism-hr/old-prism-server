package com.zuehlke.pgadmissions.rest.representation.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;

public class ProgramExtendedRepresentation extends AbstractResourceRepresentation {

    private InstitutionRepresentation institution;

    private Boolean imported;

    private PrismProgramType programType;

    private String title;

    private PrismStudyOption[] studyOptions;

    private Boolean requireProjectDefinition;

    private AdvertRepresentation advert;

    private Set<String> locations;

    private ResourceSummaryRepresentation resourceSummary;

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

    public Boolean getImported() {
        return imported;
    }

    public void setImported(Boolean imported) {
        this.imported = imported;
    }

    public PrismProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PrismStudyOption[] getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(PrismStudyOption[] studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Boolean getRequireProjectDefinition() {
        return requireProjectDefinition;
    }

    public void setRequireProjectDefinition(Boolean requireProjectDefinition) {
        this.requireProjectDefinition = requireProjectDefinition;
    }

    public AdvertRepresentation getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentation advert) {
        this.advert = advert;
    }

    public Set<String> getLocations() {
        return locations;
    }

    public void setLocations(Set<String> locations) {
        this.locations = locations;
    }

    public final ResourceSummaryRepresentation getResourceSummary() {
        return resourceSummary;
    }

    public final void setResourceSummary(ResourceSummaryRepresentation resourceSummary) {
        this.resourceSummary = resourceSummary;
    }

}
