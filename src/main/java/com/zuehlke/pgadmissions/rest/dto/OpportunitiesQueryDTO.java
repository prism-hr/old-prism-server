package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class OpportunitiesQueryDTO {

    @NotNull
    private PrismProgramCategory programCategory;

    private String keyword;

    private List<PrismProgramType> programTypes;

    private List<PrismStudyOption> studyOptions;

    private Integer feeMinimum;

    private Integer feeMaximum;

    private Integer payMinimum;

    private Integer payMaximum;

    private Integer durationMinimum;

    private Integer durationMaximum;

    private List<Integer> institutions;

    private List<Integer> programs;

    private List<Integer> projects;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;

    public PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public void setProgramCategory(PrismProgramCategory programCategory) {
        this.programCategory = programCategory;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<PrismProgramType> getProgramTypes() {
        return programTypes;
    }

    public void setProgramTypes(List<PrismProgramType> programTypes) {
        this.programTypes = programTypes;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public Integer getFeeMinimum() {
        return feeMinimum;
    }

    public void setFeeMinimum(Integer feeMinimum) {
        this.feeMinimum = feeMinimum;
    }

    public Integer getFeeMaximum() {
        return feeMaximum;
    }

    public void setFeeMaximum(Integer feeMaximum) {
        this.feeMaximum = feeMaximum;
    }

    public Integer getPayMinimum() {
        return payMinimum;
    }

    public void setPayMinimum(Integer payMinimum) {
        this.payMinimum = payMinimum;
    }

    public Integer getPayMaximum() {
        return payMaximum;
    }

    public void setPayMaximum(Integer payMaximum) {
        this.payMaximum = payMaximum;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public List<Integer> getInstitutions() {
        return institutions;
    }

    public void setInstitutions(List<Integer> institutions) {
        this.institutions = institutions;
    }

    public List<Integer> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Integer> programs) {
        this.programs = programs;
    }

    public List<Integer> getProjects() {
        return projects;
    }

    public void setProjects(List<Integer> projects) {
        this.projects = projects;
    }

    public BigDecimal getNeLat() {
        return neLat;
    }

    public void setNeLat(BigDecimal neLat) {
        this.neLat = neLat;
    }

    public BigDecimal getNeLon() {
        return neLon;
    }

    public void setNeLon(BigDecimal neLon) {
        this.neLon = neLon;
    }

    public BigDecimal getSwLat() {
        return swLat;
    }

    public void setSwLat(BigDecimal swLat) {
        this.swLat = swLat;
    }

    public BigDecimal getSwLon() {
        return swLon;
    }

    public void setSwLon(BigDecimal swLon) {
        this.swLon = swLon;
    }
}
