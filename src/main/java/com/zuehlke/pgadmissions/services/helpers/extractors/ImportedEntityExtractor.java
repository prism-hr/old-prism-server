package com.zuehlke.pgadmissions.services.helpers.extractors;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public interface ImportedEntityExtractor {

    List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions, boolean enable) throws Exception;

}
