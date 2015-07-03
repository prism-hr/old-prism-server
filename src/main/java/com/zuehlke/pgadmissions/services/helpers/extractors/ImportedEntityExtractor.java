package com.zuehlke.pgadmissions.services.helpers.extractors;

import java.util.List;

import uk.co.alumeni.prism.api.model.imported.ImportedEntity;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

public interface ImportedEntityExtractor<T extends ImportedEntity> {

    List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) throws Exception;

}
