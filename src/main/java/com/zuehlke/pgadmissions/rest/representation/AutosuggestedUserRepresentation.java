package com.zuehlke.pgadmissions.rest.representation;

public class AutosuggestedUserRepresentation {

    private String k;

    private String v;

    private String d;

    public AutosuggestedUserRepresentation(String k, String v, String d) {
        this.k = k;
        this.v = v;
        this.d = d;
    }

    public String getK() {
        return k;
    }

    public String getV() {
        return v;
    }

    public String getD() {
        return d;
    }
}
