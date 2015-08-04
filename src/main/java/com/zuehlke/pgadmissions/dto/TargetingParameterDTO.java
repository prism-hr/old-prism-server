package com.zuehlke.pgadmissions.dto;

import java.math.BigDecimal;

import com.google.common.base.Objects;

public class TargetingParameterDTO {

    private Integer concentration;

    private BigDecimal proliferation;

    public TargetingParameterDTO(Integer concentration, BigDecimal proliferation) {
        this.concentration = concentration;
        this.proliferation = proliferation;
    }

    public Integer getConcentration() {
        return concentration;
    }

    public BigDecimal getProliferation() {
        return proliferation;
    }

    public void setConcentration(Integer concentration) {
        this.concentration = concentration;
    }

    public void setProliferation(BigDecimal proliferation) {
        this.proliferation = proliferation;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(concentration, proliferation);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        TargetingParameterDTO other = (TargetingParameterDTO) object;
        return concentration.equals(other.getConcentration()) && proliferation.equals(other.getProliferation());
    }

}
