package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public abstract class ImportedEntity implements IUniqueEntity {

    public abstract Integer getId();
    
    public abstract void setId(Integer id);

    public abstract Institution getInstitution();

    public abstract void setInstitution(Institution institution);

    public abstract String getCode();

    public abstract void setCode(String code);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Boolean isEnabled();

    public abstract void setEnabled(Boolean enabled);
    
    public PrismImportedEntity getType() {
        return null;
    }
    
    public void setType(PrismImportedEntity type) {
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        PrismImportedEntity type = getType();
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties1 = Maps.newHashMap();
        properties1.put("institution", getInstitution());
        if (type != null) {
            properties1.put("importedEntityType", getType());
        }
        properties1.put("code", getCode());
        propertiesWrapper.add(properties1);
        HashMap<String, Object> properties2 = Maps.newHashMap();
        properties2.put("institution", getInstitution());
        if (type != null) {
            properties2.put("importedEntityType", getType());
        }
        properties2.put("name", getName());
        propertiesWrapper.add(properties2);
        return new ResourceSignature(propertiesWrapper);
    }

}
