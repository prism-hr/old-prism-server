package uk.co.alumeni.prism.rest.representation;

import uk.co.alumeni.prism.domain.definitions.PrismDisability;

public class DisabilityRepresentation {

    private PrismDisability id;

    private String name;

    public DisabilityRepresentation(PrismDisability id, String name) {
        this.id = id;
        this.name = name;
    }

    public PrismDisability getId() {
        return id;
    }

    public void setId(PrismDisability id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
