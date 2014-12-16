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

    private Integer minFee;

    private Integer maxFee;

    private Integer minSalary;

    private Integer maxSalary;

    private Integer minDuration;

    private Integer maxDuration;

    private List<Integer> institutions;

    private List<Integer> programs;

    private List<Integer> projects;

    private BigDecimal neLat;

    private BigDecimal neLon;

    private BigDecimal swLat;

    private BigDecimal swLon;
    
    private String lastSequenceIdentifier;

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
    
    public final Integer getMinFee() {
        return minFee;
    }

    public final void setMinFee(Integer minFee) {
        this.minFee = minFee;
    }

    public final Integer getMaxFee() {
        return maxFee;
    }

    public final void setMaxFee(Integer maxFee) {
        this.maxFee = maxFee;
    }

    public final Integer getMinSalary() {
        return minSalary;
    }

    public final void setMinSalary(Integer minSalary) {
        this.minSalary = minSalary;
    }

    public final Integer getMaxSalary() {
        return maxSalary;
    }

    public final void setMaxSalary(Integer maxSalary) {
        this.maxSalary = maxSalary;
    }

    public final Integer getMinDuration() {
        return minDuration;
    }

    public final void setMinDuration(Integer minDuration) {
        this.minDuration = minDuration;
    }

    public final Integer getMaxDuration() {
        return maxDuration;
    }

    public final void setMaxDuration(Integer maxDuration) {
        this.maxDuration = maxDuration;
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

    public final String getLastSequenceIdentifier() {
        return lastSequenceIdentifier;
    }

    public final void setLastSequenceIdentifier(String lastSequenceIdentifier) {
        this.lastSequenceIdentifier = lastSequenceIdentifier;
    }
    
}
