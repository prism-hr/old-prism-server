package com.zuehlke.pgadmissions.rest.representation;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class InstitutionDomicileRegionRepresentation {

    private String id;

    private Set<InstitutionDomicileRegionRepresentation> subRegions;

    private String regionType;

    private String name;
    
    private Integer nestedLevel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<InstitutionDomicileRegionRepresentation> getSubRegions() {
        return subRegions;
    }

    public void setSubRegions(Set<InstitutionDomicileRegionRepresentation> subRegions) {
        this.subRegions = subRegions;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final Integer getNestedLevel() {
        return nestedLevel;
    }

    public final void setNestedLevel(Integer nestedLevel) {
        this.nestedLevel = nestedLevel;
    }
}
