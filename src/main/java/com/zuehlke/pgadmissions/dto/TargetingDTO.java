package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

public class TargetingDTO {

    private Integer score;

    private Integer concentration;

    private BigDecimal profileration;

    public TargetingDTO(Integer score, Integer concentration, BigDecimal profileration) {
        this.score = score;
        this.concentration = concentration;
        this.profileration = profileration;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getConcentration() {
        return concentration;
    }

    public BigDecimal getProfileration() {
        return profileration;
    }

}
