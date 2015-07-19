package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.api.model.imported.request.ImportedAdvertDomicileRequest;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;

@Component
public class ImportedAdvertDomicileTransformer implements ImportedEntityTransformer<ImportedAdvertDomicileRequest, ImportedAdvertDomicile> {

    @Override
    public void transform(ImportedAdvertDomicileRequest concreteSource, ImportedAdvertDomicile concreteTarget) {
        concreteTarget.setCurrency(concreteSource.getCurrency());
    }

}
