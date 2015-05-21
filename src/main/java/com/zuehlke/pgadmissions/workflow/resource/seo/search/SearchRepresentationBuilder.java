package com.zuehlke.pgadmissions.workflow.resource.seo.search;

import com.zuehlke.pgadmissions.dto.SearchEngineAdvertDTO;

public interface SearchRepresentationBuilder {

    public SearchEngineAdvertDTO build(Integer resourceId) throws Exception;

}
