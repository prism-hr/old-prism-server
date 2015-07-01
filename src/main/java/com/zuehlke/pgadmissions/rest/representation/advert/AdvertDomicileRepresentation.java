package com.zuehlke.pgadmissions.rest.representation.advert;

public class AdvertDomicileRepresentation {

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
    
    public AdvertDomicileRepresentation withId(String id) {
        this.id = id;
        return this;
    }
    
    public AdvertDomicileRepresentation withName(String name) {
        this.name = name;
        return this;
    }
    
    public AdvertDomicileRepresentation withCurrency(String currency) {
        this.currency = currency;
        return this;
    }

}
