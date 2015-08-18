package com.zuehlke.pgadmissions.rest.representation.user;

public class UserRepresentationUnverified extends UserRepresentationSimple {

    private String diagnosticInformation;

    public String getDiagnosticInformation() {
        return diagnosticInformation;
    }

    public void setDiagnosticInformation(String diagnosticInformation) {
        this.diagnosticInformation = diagnosticInformation;
    }

    public UserRepresentationUnverified withDiagnosticInformation(String diagnosticInformation) {
        this.diagnosticInformation = diagnosticInformation;
        return this;
    }

}
