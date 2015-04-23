package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismLocale;

public class InstitutionDomicileDTO {

    private String id;

    private String currency;

    private PrismLocale locale;

    private String name;

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

}
