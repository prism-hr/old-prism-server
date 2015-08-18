package com.zuehlke.pgadmissions.domain.advert;

import org.apache.commons.lang3.ObjectUtils;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

public abstract class AdvertTargetResource extends AdvertTarget<ResourceParent<?>> {

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract Department getDepartment();

    public abstract void setDepartment(Department department);

    @Override
    @SuppressWarnings("unchecked")
    public ResourceParent<?> getValue() {
        return ObjectUtils.firstNonNull(getInstitution(), getDepartment());
    }

    @Override
    public void setValue(ResourceParent<?> value) {
        PrismReflectionUtils.setProperty(this, value.getResourceScope().getLowerCamelName(), value);
    }

    @Override
    public Integer getValueId() {
        return getValue().getId();
    }

    @Override
    public String getName() {
        return getValue().getName();
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("advert", getAdvert()).addProperty("institution", getInstitution()).addProperty("department", getDepartment());
    }

}
