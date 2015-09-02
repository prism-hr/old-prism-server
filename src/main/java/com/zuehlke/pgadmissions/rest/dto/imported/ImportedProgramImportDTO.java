package com.zuehlke.pgadmissions.rest.dto.imported;

import java.util.Set;

import uk.co.alumeni.prism.api.model.imported.request.ImportedProgramRequest;

public class ImportedProgramImportDTO extends ImportedProgramRequest {

    private Integer weight;

    private Set<String> jacsCodes;

    private Set<Integer> ucasSubjects;

    private Set<String> campuses;

    public ImportedProgramImportDTO() {
        return;
    }

    public ImportedProgramImportDTO(String name) {
        super(name);
    }

    public ImportedProgramImportDTO(String name, String code) {
        super(name, code);
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Set<String> getJacsCodes() {
        return jacsCodes;
    }

    public void setJacsCodes(Set<String> jacsCodes) {
        this.jacsCodes = jacsCodes;
    }

    public Set<Integer> getUcasSubjects() {
        return ucasSubjects;
    }

    public void setUcasSubjects(Set<Integer> ucasSubjects) {
        this.ucasSubjects = ucasSubjects;
    }

    public Set<String> getCampuses() {
        return campuses;
    }

    public void setCampuses(Set<String> campuses) {
        this.campuses = campuses;
    }

    public ImportedProgramImportDTO withWeight(Integer weight) {
        this.weight = weight;
        return this;
    }

    public ImportedProgramImportDTO withInstitution(Integer institution) {
        setInstitution(institution);
        return this;
    }

    public ImportedProgramImportDTO withQualificationType(Integer qualificationType) {
        setQualificationType(qualificationType);
        return this;
    }

    public ImportedProgramImportDTO withLevel(String level) {
        setLevel(level);
        return this;
    }

    public ImportedProgramImportDTO withQualification(String qualification) {
        setQualification(qualification);
        return this;
    }

    public ImportedProgramImportDTO withUcasSubjects(Set<Integer> ucasSubjects) {
        this.ucasSubjects = ucasSubjects;
        return this;
    }

    public ImportedProgramImportDTO withCampuses(final Set<String> campuses) {
        this.campuses = campuses;
        return this;
    }

    public ImportedProgramImportDTO withCode(String code) {
        setCode(code);
        return this;
    }

    public ImportedProgramImportDTO withJacsCodes(Set<String> jacsCodes) {
        setJacsCodes(jacsCodes);
        return this;
    }

}
