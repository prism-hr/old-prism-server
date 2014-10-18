package com.zuehlke.pgadmissions.rest.dto;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

import java.util.Set;

public class AdvertCategoriesDTO {

    private Set<PrismAdvertDomain> domains;

    private Set<PrismAdvertFunction> functions;

    private Set<PrismAdvertIndustry> industries;

    private Set<String> competencies;

    private Set<String> themes;

    private Set<Integer> targetInstitutions;

    private Set<PrismProgramType> targetProgramTypes;

    public Set<PrismAdvertDomain> getDomains() {
        return domains;
    }

    public void setDomains(Set<PrismAdvertDomain> domains) {
        this.domains = domains;
    }

    public Set<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public Set<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public Set<String> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Set<String> competencies) {
        this.competencies = competencies;
    }

    public Set<String> getThemes() {
        return themes;
    }

    public void setThemes(Set<String> themes) {
        this.themes = themes;
    }

    public Set<Integer> getTargetInstitutions() {
        return targetInstitutions;
    }

    public void setTargetInstitutions(Set<Integer> targetInstitutions) {
        this.targetInstitutions = targetInstitutions;
    }

    public Set<PrismProgramType> getTargetProgramTypes() {
        return targetProgramTypes;
    }

    public void setTargetProgramTypes(Set<PrismProgramType> targetProgramTypes) {
        this.targetProgramTypes = targetProgramTypes;
    }
}
