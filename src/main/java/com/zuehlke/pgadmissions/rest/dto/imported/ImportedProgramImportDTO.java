package com.zuehlke.pgadmissions.rest.dto.imported;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

public class ImportedProgramImportDTO extends ImportedProgramRequest {
    
    private Integer weight;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public ImportedProgramImportDTO() {
    }

    public ImportedProgramImportDTO(String name) {
        super(name);
    }

    public ImportedProgramImportDTO(String name, String code) {
        super(name, code);
    }

    public ImportedProgramImportDTO withWeight(final Integer weight) {
        this.weight = weight;
        return this;
    }

    public ImportedProgramImportDTO withInstitution(final Integer institution) {
        setInstitution(institution);
        return this;
    }

    public ImportedProgramImportDTO withQualificationType(final Integer qualificationType) {
        setQualificationType(qualificationType);
        return this;
    }

    public ImportedProgramImportDTO withSubjectAreas(final java.util.Set<String> subjectAreas) {
        setSubjectAreas(subjectAreas);
        return this;
    }

    public ImportedProgramImportDTO withLevel(final String level) {
        setLevel(level);
        return this;
    }

    public ImportedProgramImportDTO withQualification(final String qualification) {
        setQualification(qualification);
        return this;
    }

}
