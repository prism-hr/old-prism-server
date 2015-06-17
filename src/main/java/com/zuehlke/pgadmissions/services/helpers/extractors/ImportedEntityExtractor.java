package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import java.util.List;

public interface ImportedEntityExtractor {

    List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception;

}
