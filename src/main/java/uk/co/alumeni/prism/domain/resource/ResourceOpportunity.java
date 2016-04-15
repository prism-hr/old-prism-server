package uk.co.alumeni.prism.domain.resource;

import java.util.Set;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;

public abstract class ResourceOpportunity extends ResourceParent implements ResourceOpportunityDefinition<OpportunityType> {

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    @Override
    public abstract OpportunityType getOpportunityType();

    @Override
    public abstract void setOpportunityType(OpportunityType opportunityType);

    public abstract LocalDate getAvailableDate();

    public abstract void setAvailableDate(LocalDate availableDate);

    public abstract Set<ResourceStudyOption> getResourceStudyOptions();

    public abstract void setResourceStudyOptions(Set<ResourceStudyOption> resourceStudyOptions);

    public void addResourceStudyOption(ResourceStudyOption resourceStudyOption) {
        getResourceStudyOptions().add(resourceStudyOption);
    }

    @Override
    public UniqueEntity.EntitySignature getEntitySignature() {
        return super.getEntitySignature()
                .addProperty("institution", getInstitution())
                .addProperty("opportunityType", getOpportunityType());
    }

}
