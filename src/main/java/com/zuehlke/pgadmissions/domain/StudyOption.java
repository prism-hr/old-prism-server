package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@DiscriminatorValue("STUDY_OPTION")
public class StudyOption extends AbstractImportedEntity implements IUniqueEntity {

    public StudyOption withInstitution(Institution institution) {
        setInstitution(institution);
        return this;
    }

    public StudyOption withCode(String code) {
        setCode(code);
        return this;
    }

    public StudyOption withName(String name) {
        setName(name);
        return this;
    }

    public StudyOption withEnabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("institution", getInstitution());
        properties.put("code", getCode());
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }

}
