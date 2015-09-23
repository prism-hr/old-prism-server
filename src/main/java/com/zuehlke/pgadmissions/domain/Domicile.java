package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;

@Entity
@Table(name = "domicile")
public class Domicile extends Definition<PrismDomicile> {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismDomicile id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Override
    public PrismDomicile getId() {
        return id;
    }

    @Override
    public void setId(PrismDomicile id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Domicile withId(PrismDomicile id) {
        this.id = id;
        return this;
    }

    public Domicile withName(String name) {
        this.name = name;
        return this;
    }

    public Domicile withCurrency(final String currency) {
        this.currency = currency;
        return this;
    }

    public Domicile withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

}
