package com.zuehlke.pgadmissions.rest.dto.resource;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class InstitutionImportDTO {

    @NotEmpty
    @Size(max = 255)
    private String name;

    private List<Integer> ucasIds;

    @Size(max = 20)
    private String facebookId;

    @Min(1)
    @Max(10000)
    private Integer hesaId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public InstitutionImportDTO withUcasIds(final List<Integer> ucasIds) {
        this.ucasIds = ucasIds;
        return this;
    }

}
