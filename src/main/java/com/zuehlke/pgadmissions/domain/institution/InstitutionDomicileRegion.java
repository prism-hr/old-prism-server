package com.zuehlke.pgadmissions.domain.institution;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;

@Entity
@Table(name = "INSTITUTION_DOMICILE_REGION", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_domicile_id", "parent_region_id",
        "region_type", "name" }) })
public class InstitutionDomicileRegion extends GeocodableLocation {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id")
    private InstitutionDomicile domicile;

    @ManyToOne
    @JoinColumn(name = "parent_region_id")
    private InstitutionDomicileRegion parentRegion;

    @Column(name = "nested_path", nullable = false)
    private String nestedPath;

    @Column(name = "nested_level", nullable = false)
    private Integer nestedLevel;

    @Column(name = "region_type", nullable = false)
    private String regionType;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private GeographicLocation location;

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

    public final String getNestedPath() {
        return nestedPath;
    }

    public final void setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
    }

    public final Integer getNestedLevel() {
        return nestedLevel;
    }

    public final void setNestedLevel(Integer nestedLevel) {
        this.nestedLevel = nestedLevel;
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

    @Override
    public final GeographicLocation getLocation() {
        return location;
    }

    @Override
    public final void setLocation(GeographicLocation location) {
        this.location = location;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
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

    public InstitutionDomicileRegion withName(String name) {
        this.name = name;
        return this;
    }

    public InstitutionDomicileRegion withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    public InstitutionDomicileRegion withNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
        return this;
    }

    public InstitutionDomicileRegion withNestedLevel(Integer nestedLevel) {
        this.nestedLevel = nestedLevel;
        return this;
    }

    public InstitutionDomicileRegion withRegionType(String regionType) {
        this.regionType = regionType;
        return this;
    }

    public InstitutionDomicileRegion withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String getLocationString() {
        return buildLocationString(name, parentRegion == null ? null : parentRegion.getName(), domicile.getName());
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("domicile", domicile).addProperty("parentRegion", parentRegion).addProperty("regionType", regionType)
                .addProperty("name", name);
    }

}
