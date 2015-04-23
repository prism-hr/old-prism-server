package com.zuehlke.pgadmissions.domain.institution;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.UniqueEntity;

@Entity
@Table(name = "INSTITUTION_DOMICILE")
public class InstitutionDomicile implements UniqueEntity {

    @Id
    private String id;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;
    
    @OneToMany(mappedBy = "institutionDomicile")
    private Set<InstitutionDomicileName> names = Sets.newHashSet();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Set<InstitutionDomicileName> getNames() {
        return names;
    }

    public InstitutionDomicile withId(String id) {
        this.id = id;
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

    public InstitutionDomicile addName(InstitutionDomicileName name) {
        this.names.add(name);
        return this;
    }
    
    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("id", id);
    }

}
