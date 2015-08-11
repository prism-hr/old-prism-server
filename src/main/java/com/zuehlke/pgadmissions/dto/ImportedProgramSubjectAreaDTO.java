package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import jersey.repackaged.com.google.common.base.Objects;

import com.zuehlke.pgadmissions.domain.definitions.PrismTargetingMatchType;

public class ImportedProgramSubjectAreaDTO {

    private Integer id;

    private PrismTargetingMatchType matchType;

    private BigDecimal weight;

    private BigDecimal confidence;

    public ImportedProgramSubjectAreaDTO(Integer id, PrismTargetingMatchType matchType, BigDecimal weight, BigDecimal confidence) {
        this.id = id;
        this.matchType = matchType;
        this.weight = weight;
        this.confidence = confidence;
    }

    public Integer getId() {
        return id;
    }

    public PrismTargetingMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(PrismTargetingMatchType matchType) {
        this.matchType = matchType;
    }
    
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    
    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
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
