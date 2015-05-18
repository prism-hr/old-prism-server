package com.zuehlke.pgadmissions.workflow.resource.seo.search;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;
import com.zuehlke.pgadmissions.services.InstitutionService;

@Component
public class SystemSearchRepresentationBuilder implements SearchRepresentationBuilder {

    @Inject
    private InstitutionService institutionService;
    
    @Override
    public SearchEngineAdvertDTO build(Integer resourceId) throws Exception {
        return new SearchEngineAdvertDTO().withRelatedInstitutions(institutionService.getActiveInstitutions());
    }

}
