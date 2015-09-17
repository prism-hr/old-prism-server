package com.zuehlke.pgadmissions.rest.dto.imported;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

public class ImportedInstitutionImportDTO extends ImportedInstitutionRequest {

    private List<Integer> ucasIds;

    @Size(max = 20)
    private String facebookId;

    @Min(1)
    @Max(10000)
    private Integer hesaId;

    @Min(0)
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

    public List<Integer> getUcasIds() {
        return ucasIds;
    }

    public void setUcasIds(List<Integer> ucasIds) {
        this.ucasIds = ucasIds;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getHesaId() {
        return hesaId;
    }

    public void setHesaId(Integer hesaId) {
        this.hesaId = hesaId;
    }

    public Integer getStudentsNumber() {
        return studentsNumber;
    }

    public void setStudentsNumber(Integer studentsNumber) {
        this.studentsNumber = studentsNumber;
    }

    public ImportedInstitutionImportDTO withUcasIds(final List<Integer> ucasIds) {
        this.ucasIds = ucasIds;
        return this;
    }

}
