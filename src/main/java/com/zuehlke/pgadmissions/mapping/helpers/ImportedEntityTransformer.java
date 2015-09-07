package com.zuehlke.pgadmissions.mapping.helpers;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntity;

public interface ImportedEntityTransformer<T extends uk.co.alumeni.prism.api.model.imported.request.ImportedEntityRequest, U extends ImportedEntity<?, ?>> {

    public void transform(T concreteSource, U concreteTarget);

}
