package com.zuehlke.pgadmissions.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "INSTITUTION_DOMICILE_REGION", uniqueConstraints = {@UniqueConstraint(columnNames = {"institution_domicile_id", "parent_region_id", "region_type", "name"})})
public class InstitutionDomicileRegion {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id")
    private InstitutionDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "parent_region_id")
    private InstitutionDomicileRegion parentRegion;

    @Column(name = "region_type", nullable = false)
    private String regionType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "other_name")
    private String otherName;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "parentRegion")
    private Set<InstitutionDomicileRegion> subRegions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InstitutionDomicile getDomicile() {
        return domicile;
    }

    public void setDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
    }

    public InstitutionDomicileRegion getParentRegion() {
        return parentRegion;
    }

    public void setParentRegion(InstitutionDomicileRegion parentRegion) {
        this.parentRegion = parentRegion;
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

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<InstitutionDomicileRegion> getSubRegions() {
        return subRegions;
    }

    public InstitutionDomicileRegion withId(String id) {
        this.id = id;
        return this;
    }

    public InstitutionDomicileRegion withDomicile(InstitutionDomicile domicile) {
        this.domicile = domicile;
        return this;
    }

    public InstitutionDomicileRegion withParentRegion(InstitutionDomicileRegion parentRegion) {
        this.parentRegion = parentRegion;
        return this;
    }

    public InstitutionDomicileRegion withRegionType(String regionType) {
        this.regionType = regionType;
        return this;
    }

    public InstitutionDomicileRegion withName(String name) {
        this.name = name;
        return this;
    }

    public InstitutionDomicileRegion withOtherName(String otherName) {
        this.otherName = otherName;
        return this;
    }

    public InstitutionDomicileRegion withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
