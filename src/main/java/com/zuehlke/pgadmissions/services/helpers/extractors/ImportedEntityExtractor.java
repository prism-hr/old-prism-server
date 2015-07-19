package com.zuehlke.pgadmissions.services.helpers.extractors;

import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest;

import java.util.List;

public interface ImportedEntityExtractor<T extends ImportedEntityRequest> {

    List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable);

}
