package com.zuehlke.pgadmissions.domain.resource;

import java.util.Set;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;

public abstract class ResourceOpportunity extends ResourceParent {

    public abstract OpportunityType getOpportunityType();

    public abstract void setOpportunityType(OpportunityType opportunityType);

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    public abstract Boolean getImported();

    public abstract Set<ResourceStudyOption> getStudyOptions();

    public void addStudyOption(ResourceStudyOption studyOption) {
        getStudyOptions().add(studyOption);
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", getInstitution()).addProperty("partner", getPartner())
                .addProperty("opportunityType", getOpportunityType()).addProperty("title", getTitle());
    }

}
