package com.zuehlke.pgadmissions.mapping.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.services.ImportedEntityService;

@Component
public class ImportedInstitutionTransformer implements
        ImportedEntityTransformer<uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest, ImportedInstitution> {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void transform(uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest concreteSource, ImportedInstitution concreteTarget) {
        concreteTarget.setDomicile((ImportedEntitySimple) importedEntityService.getById(ImportedEntitySimple.class, concreteSource.getDomicile()));
        concreteTarget.setUcasId(concreteSource.getUcasId());
        concreteTarget.setFacebookId(concreteSource.getFacebookId());
    }

}
