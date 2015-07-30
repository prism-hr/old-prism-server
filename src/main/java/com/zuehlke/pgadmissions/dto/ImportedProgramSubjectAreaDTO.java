package com.zuehlke.pgadmissions.dto;

import jersey.repackaged.com.google.common.base.Objects;

public class ImportedProgramSubjectAreaDTO {

    private Integer id;

    private Integer weight;

    private Integer confidence;

    public ImportedProgramSubjectAreaDTO(Integer id, Integer weight) {
        this.id = id;
        this.weight = weight;
    }

    public ImportedProgramSubjectAreaDTO(Integer id, Integer weight, Integer confidence) {
        this(id, weight);
        this.confidence = confidence;
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getConfidence() {
        return confidence;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        ImportedProgramSubjectAreaDTO other = (ImportedProgramSubjectAreaDTO) object;
        return Objects.equal(id, other.getId());
    }

}
