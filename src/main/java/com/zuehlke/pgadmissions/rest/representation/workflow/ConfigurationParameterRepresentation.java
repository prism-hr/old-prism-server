package com.zuehlke.pgadmissions.rest.representation.workflow;

public class ConfigurationParameterRepresentation {

    private Enum<?> id;
    
    private Boolean rangeSpecifiation;

    public final Enum<?> getId() {
        return id;
    }

    public final void setId(Enum<?> id) {
        this.id = id;
    }

    public final Boolean getRangeSpecifiation() {
        return rangeSpecifiation;
    }

    public final void setRangeSpecifiation(Boolean rangeSpecifiation) {
        this.rangeSpecifiation = rangeSpecifiation;
    }
    
}
