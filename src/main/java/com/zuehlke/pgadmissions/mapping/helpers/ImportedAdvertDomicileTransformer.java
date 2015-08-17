package com.zuehlke.pgadmissions.mapping.helpers;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;

import uk.co.alumeni.prism.api.model.imported.request.ImportedAdvertDomicileRequest;

@Component
public class ImportedAdvertDomicileTransformer implements ImportedEntityTransformer<ImportedAdvertDomicileRequest, ImportedAdvertDomicile> {

    @Override
    public void transform(ImportedAdvertDomicileRequest concreteSource, ImportedAdvertDomicile concreteTarget) {
        concreteTarget.setCurrency(concreteSource.getCurrency());
    }

}
