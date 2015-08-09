package com.zuehlke.pgadmissions.rest.representation.resource;

public abstract class ResourceParentDivisionRepresentation extends ResourceParentRepresentation {

    private String importedCode;

    @Override
    public String getImportedCode() {
        return importedCode;
    }

    @Override
    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

}
