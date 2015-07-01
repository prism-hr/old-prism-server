package com.zuehlke.pgadmissions.domain.institution;

import com.zuehlke.pgadmissions.domain.UniqueEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "institution_domicile")
public class InstitutionDomicile implements UniqueEntity {

    @Id
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

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

    public InstitutionDomicile withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", id);
    }

}
