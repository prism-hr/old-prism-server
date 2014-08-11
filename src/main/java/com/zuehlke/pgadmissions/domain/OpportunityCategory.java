package com.zuehlke.pgadmissions.domain;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "ADVERT_OPPORTUNITY_CATEGORY")
public class OpportunityCategory implements IUniqueEntity {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private OpportunityCategory parentCategory;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public OpportunityCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(OpportunityCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public OpportunityCategory withId(Integer id) {
        this.id = id;
        return this;
    }

    public OpportunityCategory withParentCategory(OpportunityCategory parentCategory) {
        this.parentCategory = parentCategory;
        return this;
    }

    public OpportunityCategory withName(String name) {
        this.name = name;
        return this;
    }

    public OpportunityCategory withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        List<HashMap<String, Object>> propertiesWrapper = Lists.newArrayList();
        HashMap<String, Object> properties = Maps.newHashMap();
        properties.put("name", name);
        propertiesWrapper.add(properties);
        return new ResourceSignature(propertiesWrapper);
    }
    
}
