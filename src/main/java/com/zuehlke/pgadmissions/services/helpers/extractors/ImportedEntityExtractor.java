package com.zuehlke.pgadmissions.services.helpers.extractors;

import java.util.List;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;

import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

public interface ImportedEntityExtractor<T extends ImportedEntityRequest> {

    List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable);

}
