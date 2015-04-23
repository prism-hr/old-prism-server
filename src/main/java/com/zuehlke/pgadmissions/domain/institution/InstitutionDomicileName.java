package com.zuehlke.pgadmissions.domain.institution;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

@Entity
@Table(name = "INSTITUTION_DOMICILE_NAME", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_domicile_id", "locale" }),
        @UniqueConstraint(columnNames = { "institution_domicile_id", "system_default" }) })
public class InstitutionDomicileName implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_domicile_id", nullable = false)
    private InstitutionDomicile institutionDomicile;

    @Column(name = "locale", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismLocale locale;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "system_default", nullable = false)
    private Boolean systemDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InstitutionDomicile getInstitutionDomicile() {
        return institutionDomicile;
    }

    public void setInstitutionDomicile(InstitutionDomicile institutionDomicile) {
        this.institutionDomicile = institutionDomicile;
    }

    public PrismLocale getLocale() {
        return locale;
    }

    public void setLocale(PrismLocale locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSystemDefault() {
        return systemDefault;
    }

    public void setSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
    }

    public InstitutionDomicileName withInstitutionDomicile(InstitutionDomicile institutionDomicile) {
        this.institutionDomicile = institutionDomicile;
        return this;
    }

    public InstitutionDomicileName withLocale(PrismLocale locale) {
        this.locale = locale;
        return this;
    }

    public InstitutionDomicileName withName(String name) {
        this.name = name;
        return this;
    }
    
    public InstitutionDomicileName withSystemDefault(Boolean systemDefault) {
        this.systemDefault = systemDefault;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institutionDomicile", institutionDomicile).addProperty("locale", locale);
    }

}
