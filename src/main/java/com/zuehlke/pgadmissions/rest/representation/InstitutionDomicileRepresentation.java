package com.zuehlke.pgadmissions.rest.representation;

import com.google.common.base.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final InstitutionDomicileRepresentation other = (InstitutionDomicileRepresentation) object;
        return Objects.equal(id, other.getId());
    }

}
