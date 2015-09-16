package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

public abstract class ResourceOpportunity extends ResourceParentDivision implements
        ResourceOpportunityDefinition<Advert, ImportedEntitySimple> {

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    @Override
    public abstract Boolean getRequirePositionDefinition();

    @Override
    public abstract void setRequirePositionDefinition(Boolean requirePositionDefinition);

    @Override
    public abstract ImportedEntitySimple getOpportunityType();

    @Override
    public abstract void setOpportunityType(ImportedEntitySimple opportunityType);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    public abstract Set<ResourceStudyOption> getInstanceGroups();

    public abstract void setInstanceGroups(Set<ResourceStudyOption> instanceGroups);

    public void addStudyOption(ResourceStudyOption instanceGroup) {
        getInstanceGroups().add(instanceGroup);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature()
                .addProperty("institution", getInstitution())
                .addProperty("opportunityType", getOpportunityType());
    }

}
