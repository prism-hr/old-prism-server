package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;

public class DomicileRepresentation {

    private PrismDomicile id;

    private String name;

    private String currency;

    public DomicileRepresentation(PrismDomicile id, String name, String currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
    }

    public PrismDomicile getId() {
        return id;
    }

    public void setId(PrismDomicile id) {
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

}
