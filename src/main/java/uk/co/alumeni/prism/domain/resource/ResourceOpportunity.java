package uk.co.alumeni.prism.domain.resource;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;
import uk.co.alumeni.prism.domain.UniqueEntity;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;

import java.util.Set;

public abstract class ResourceOpportunity extends ResourceParent implements ResourceOpportunityDefinition<OpportunityType> {

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    @Override
    public abstract OpportunityType getOpportunityType();

    @Override
    public abstract void setOpportunityType(OpportunityType opportunityType);

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
