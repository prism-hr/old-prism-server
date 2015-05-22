package com.zuehlke.pgadmissions.services.helpers.extractors;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

public interface ImportedEntityExtractor {

    public List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception;

}
