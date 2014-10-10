package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;

public class AdvertFilterMetadataDTO {
    
    private List<PrismAdvertDomain> domains = Lists.newArrayList();
    
    private List<PrismAdvertIndustry> industries = Lists.newArrayList();
    
    private List<PrismAdvertFunction> functions = Lists.newArrayList();
    
    private List<Integer> targetInstitutions = Lists.newArrayList();
    
    private List<PrismProgramType> targetProgramTypes = Lists.newArrayList();

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
