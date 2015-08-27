package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.resource.department.Department;

import uk.co.alumeni.prism.api.model.resource.ResourceOpportunityDefinition;

public abstract class ResourceOpportunity extends ResourceParentDivision implements
        ResourceOpportunityDefinition<Advert, ImportedEntitySimple, AdvertStudyOption> {

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    @Override
    public abstract Boolean getRequirePositionDefinition();

    @Override
    public abstract void setRequirePositionDefinition(Boolean requirePositionDefinition);

    public abstract PrismOpportunityCategory getOpportunityCategory();

    public abstract void setOpportunityCategory(PrismOpportunityCategory opportunityCategory);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("institution", getInstitution()).addProperty("opportunityType", getOpportunityType());
    }

}
