package uk.co.alumeni.prism.dto;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;

public class ResourceSimpleDTO extends ResourceIdentityDTO {

    private String code;

    private String importedCode;

    private PrismState stateId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImportedCode() {
        return importedCode;
    }

    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public PrismState getStateId() {
        return stateId;
    }

    public void setStateId(PrismState stateId) {
        this.stateId = stateId;
    }

}
