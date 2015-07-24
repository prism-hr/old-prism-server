package com.zuehlke.pgadmissions.rest.dto.imported;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

public class ImportedProgramInternalRequest extends ImportedProgramRequest {

    private Integer weight;

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public ImportedProgramInternalRequest() {
    }

    public ImportedProgramInternalRequest(String name) {
        super(name);
    }

    public ImportedProgramInternalRequest(String name, String code) {
        super(name, code);
    }

    public ImportedProgramInternalRequest withWeight(final Integer weight) {
        this.weight = weight;
        return this;
    }

    public ImportedProgramInternalRequest withInstitution(final Integer institution) {
        setInstitution(institution);
        return this;
    }

    public ImportedProgramInternalRequest withQualificationType(final Integer qualificationType) {
        setQualificationType(qualificationType);
        return this;
    }

    public ImportedProgramInternalRequest withSubjectAreas(final java.util.Set<String> subjectAreas) {
        setSubjectAreas(subjectAreas);
        return this;
    }

    public ImportedProgramInternalRequest withLevel(final String level) {
        setLevel(level);
        return this;
    }

    public ImportedProgramInternalRequest withQualification(final String qualification) {
        setQualification(qualification);
        return this;
    }

    public ImportedProgramInternalRequest withHomepage(final String homepage) {
        setHomepage(homepage);
        return this;
    }


}
