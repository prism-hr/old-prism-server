package uk.co.alumeni.prism.rest.representation;

import uk.co.alumeni.prism.domain.definitions.PrismEthnicity;

public class EthnicityRepresentation {

    private PrismEthnicity id;

    private String name;

    public EthnicityRepresentation(PrismEthnicity id, String name) {
        this.id = id;
        this.name = name;
    }

    public PrismEthnicity getId() {
        return id;
    }

    public void setId(PrismEthnicity id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
