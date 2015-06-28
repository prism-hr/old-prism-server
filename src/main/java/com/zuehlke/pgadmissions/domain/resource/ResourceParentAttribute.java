package com.zuehlke.pgadmissions.domain.resource;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.institution.Institution;

public abstract class ResourceParentAttribute extends ResourceOpportunityAttribute {

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    @Override
    public ResourceParent getResource() {
        return ObjectUtils.firstNonNull(super.getResource(), getInstitution());
    }

}
