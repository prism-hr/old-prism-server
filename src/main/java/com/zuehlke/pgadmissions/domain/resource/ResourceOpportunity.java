package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.workflow.user.PrismUserReassignmentProcessor;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

public abstract class ResourceOpportunity<T extends PrismUserReassignmentProcessor> extends ResourceParentDivision<T>implements
        ResourceOpportunityDefinition<Advert, ImportedEntitySimple, ResourceStudyOption> {

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

    public abstract PrismOpportunityCategory getOpportunityCategory();

    public abstract void setOpportunityCategory(PrismOpportunityCategory opportunityCategory);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    @Override
    public abstract Set<ResourceStudyOption> getInstanceGroups();

    @Override
    public abstract void setInstanceGroups(Set<ResourceStudyOption> instanceGroups);

    public abstract Set<ResourceStudyLocation> getStudyLocations();

    public void addStudyOption(ResourceStudyOption instanceGroup) {
        getInstanceGroups().add(instanceGroup);
    }

    public void addStudyLocation(ResourceStudyLocation studyLocation) {
        getStudyLocations().add(studyLocation);
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("institution", getInstitution()).addProperty("opportunityType", getOpportunityType());
    }

}
