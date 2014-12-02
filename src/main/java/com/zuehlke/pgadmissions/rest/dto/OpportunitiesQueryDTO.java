package com.zuehlke.pgadmissions.rest.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismProgramCategory;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class OpportunitiesQueryDTO {

    @NotNull
    private PrismProgramCategory programCategory;

    @NotNull
    private OpportunityLocationQueryDTO location;

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

    public PrismProgramCategory getProgramCategory() {
        return programCategory;
    }

    public void setProgramCategory(PrismProgramCategory programCategory) {
        this.programCategory = programCategory;
    }

    public final OpportunityLocationQueryDTO getLocation() {
        return location;
    }

    public final void setLocation(OpportunityLocationQueryDTO location) {
        this.location = location;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public final List<PrismProgramType> getProgramTypes() {
        return programTypes;
    }

    public final void setProgramTypes(List<PrismProgramType> programTypes) {
        this.programTypes = programTypes;
    }

    public final List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public final void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public final Integer getFeeMinimum() {
        return feeMinimum;
    }

    public final void setFeeMinimum(Integer feeMinimum) {
        this.feeMinimum = feeMinimum;
    }

    public final Integer getFeeMaximum() {
        return feeMaximum;
    }

    public final void setFeeMaximum(Integer feeMaximum) {
        this.feeMaximum = feeMaximum;
    }

    public final Integer getPayMinimum() {
        return payMinimum;
    }

    public final void setPayMinimum(Integer payMinimum) {
        this.payMinimum = payMinimum;
    }

    public final Integer getPayMaximum() {
        return payMaximum;
    }

    public final void setPayMaximum(Integer payMaximum) {
        this.payMaximum = payMaximum;
    }

    public final Integer getDurationMinimum() {
        return durationMinimum;
    }

    public final void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public final Integer getDurationMaximum() {
        return durationMaximum;
    }

    public final void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public final List<Integer> getInstitutions() {
        return institutions;
    }

    public final void setInstitutions(List<Integer> institutions) {
        this.institutions = institutions;
    }

    public final List<Integer> getPrograms() {
        return programs;
    }

    public final void setPrograms(List<Integer> programs) {
        this.programs = programs;
    }

    public final List<Integer> getProjects() {
        return projects;
    }

    public final void setProjects(List<Integer> projects) {
        this.projects = projects;
    }
    
    public static class OpportunityLocationQueryDTO {
        
        private BigDecimal locationViewNeX;
        
        private BigDecimal locationViewNeY;
        
        private BigDecimal locationViewSwX;

        private BigDecimal locationViewSwY;

        public final BigDecimal getLocationViewNeX() {
            return locationViewNeX;
        }

        public final void setLocationViewNeX(BigDecimal locationViewNeX) {
            this.locationViewNeX = locationViewNeX;
        }

        public final BigDecimal getLocationViewNeY() {
            return locationViewNeY;
        }

        public final void setLocationViewNeY(BigDecimal locationViewNeY) {
            this.locationViewNeY = locationViewNeY;
        }

        public final BigDecimal getLocationViewSwX() {
            return locationViewSwX;
        }

        public final void setLocationViewSwX(BigDecimal locationViewSwX) {
            this.locationViewSwX = locationViewSwX;
        }

        public final BigDecimal getLocationViewSwY() {
            return locationViewSwY;
        }

        public final void setLocationViewSwY(BigDecimal locationViewSwY) {
            this.locationViewSwY = locationViewSwY;
        }
        
    }

}
