package com.zuehlke.pgadmissions.rest.dto.imported;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

public class ImportedInstitutionImportDTO extends ImportedInstitutionRequest {

    @Min(1)
    @Max(999999999)
    private Integer ucasId;

    @Size(max = 20)
    private String facebookId;

    private Integer studentsNumber;

    public ImportedInstitutionImportDTO() {
        return;
    }

    public ImportedInstitutionImportDTO(String name) {
        super(name);
    }

    public ImportedInstitutionImportDTO(String name, String code) {
        super(name, code);
    }

    public Integer getUcasId() {
        return ucasId;
    }

    public void setUcasId(Integer ucasId) {
        this.ucasId = ucasId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getStudentsNumber() {
        return studentsNumber;
    }

    public void setStudentsNumber(Integer studentsNumber) {
        this.studentsNumber = studentsNumber;
    }

    public ImportedInstitutionImportDTO withUcasId(Integer ucasId) {
        this.ucasId = ucasId;
        return this;
    }

}
