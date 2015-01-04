package com.zuehlke.pgadmissions.domain.institution;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;

@Entity
@Table(name = "INSTITUTION_DOMICILE")
public class InstitutionDomicile extends GeocodableLocation {

    @Id
    private String id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Embedded
    private GeographicLocation location;


    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public InstitutionDomicile withId(String id) {
        this.id = id;
        return this;
    }

    public InstitutionDomicile withName(String name) {
        this.name = name;
        return this;
    }

    public InstitutionDomicile withCurrency(final String currency) {
        this.currency = currency;
        return this;
    }

    public InstitutionDomicile withLocation(GeographicLocation location) {
        this.location = location;
        return this;
    }

    public InstitutionDomicile withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public String getLocationString() {
        return name;
    }

}
