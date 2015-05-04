package com.zuehlke.pgadmissions.domain.resource;

import com.zuehlke.pgadmissions.domain.department.Department;
import com.zuehlke.pgadmissions.domain.imported.OpportunityType;
import com.zuehlke.pgadmissions.domain.institution.Institution;

public abstract class ResourceOpportunity extends ResourceParent {

    public abstract Institution getPartner();

    public abstract void setPartner(Institution institution);

    public abstract OpportunityType getOpportunityType();

    public abstract void setOpportunityType(OpportunityType opportunityType);

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    public abstract Integer getDurationMinimum();

    public abstract void setDurationMinimum(Integer minimum);

    public abstract Integer getDurationMaximum();

    public abstract void setDurationMaximum(Integer maximum);

    public abstract Boolean getImported();

}
