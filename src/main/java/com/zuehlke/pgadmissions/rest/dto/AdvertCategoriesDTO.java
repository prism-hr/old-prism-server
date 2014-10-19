package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class AdvertCategoriesDTO {

    private List<PrismAdvertDomain> domains;

    private List<PrismAdvertIndustry> industries;

    private List<PrismAdvertFunction> functions;
    
    private List<String> competencies;
    
    private List<String> themes;

    private List<Integer> targetInstitutions;

    private List<PrismProgramType> targetProgramTypes;

    public final List<PrismAdvertDomain> getDomains() {
        return domains;
    }

    public final void setDomains(List<PrismAdvertDomain> domains) {
        this.domains = domains;
    }

    public final List<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public final void setIndustries(List<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public final List<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public final void setFunctions(List<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public final List<String> getCompetencies() {
        return competencies;
    }

    public final void setCompetencies(List<String> competencies) {
        this.competencies = competencies;
    }

    public final List<String> getThemes() {
        return themes;
    }

    public final void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public final List<Integer> getTargetInstitutions() {
        return targetInstitutions;
    }

    public final void setTargetInstitutions(List<Integer> targetInstitutions) {
        this.targetInstitutions = targetInstitutions;
    }

    public final List<PrismProgramType> getTargetProgramTypes() {
        return targetProgramTypes;
    }

    public final void setTargetProgramTypes(List<PrismProgramType> targetProgramTypes) {
        this.targetProgramTypes = targetProgramTypes;
    }

}
