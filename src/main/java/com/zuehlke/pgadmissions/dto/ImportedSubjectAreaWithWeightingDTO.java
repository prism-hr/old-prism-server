package com.zuehlke.pgadmissions.dto;

import jersey.repackaged.com.google.common.base.Objects;

public class ImportedSubjectAreaWithWeightingDTO {

    private Integer id;

    private String code;

    private Integer weight;

    public ImportedSubjectAreaWithWeightingDTO(Integer id, String code, Integer weight) {
        this.id = id;
        this.code = code;
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public Integer getWeight() {
        return weight;
    }

    public Integer getSpecificity() {
        int specificity = 0;
        for (int i = 0; i < code.length() - 1; i++) {
            Character character = code.charAt(i);
            if (Character.isDigit(character) && !character.toString().equals("0")) {
                specificity++;
            }
        }
        return specificity;
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
        ImportedSubjectAreaWithWeightingDTO other = (ImportedSubjectAreaWithWeightingDTO) object;
        return Objects.equal(id, other.getId());
    }

}
