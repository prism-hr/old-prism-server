package com.zuehlke.pgadmissions.rest.representation;

public class InstitutionDomicileRepresentation {

    private String id;

    private String name;

    private String currency;

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
    
    public InstitutionDomicileRepresentation withId(String id) {
        this.id = id;
        return this;
    }
    
    public InstitutionDomicileRepresentation withName(String name) {
        this.name = name;
        return this;
    }
    
    public InstitutionDomicileRepresentation withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

}
