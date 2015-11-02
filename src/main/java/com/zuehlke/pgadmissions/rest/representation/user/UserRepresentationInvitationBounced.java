package com.zuehlke.pgadmissions.rest.representation.user;

public class UserRepresentationInvitationBounced extends UserRepresentationSimple {

    private String diagnosticInformation;

    public String getDiagnosticInformation() {
        return diagnosticInformation;
    }

    public void setDiagnosticInformation(String diagnosticInformation) {
        this.diagnosticInformation = diagnosticInformation;
    }

    public UserRepresentationInvitationBounced withDiagnosticInformation(String diagnosticInformation) {
        this.diagnosticInformation = diagnosticInformation;
        return this;
    }

}
