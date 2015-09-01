package com.zuehlke.pgadmissions.mapping.helpers;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import javax.inject.Inject;

@Component
public class ImportedInstitutionTransformer<T extends ImportedInstitutionRequest> implements ImportedEntityTransformer<T, ImportedInstitution> {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void transform(T concreteSource, ImportedInstitution concreteTarget) {
        concreteTarget.setDomicile((ImportedEntitySimple) importedEntityService.getById(ImportedEntitySimple.class, concreteSource.getDomicile()));
        if (concreteSource.getClass().equals(ImportedInstitutionImportDTO.class)) {
            concreteTarget.setUcasId(((ImportedInstitutionImportDTO) concreteSource).getUcasId());
            concreteTarget.setFacebookId(((ImportedInstitutionImportDTO) concreteSource).getFacebookId());
            concreteTarget.setHesaId(((ImportedInstitutionImportDTO) concreteSource).getHesaId());
        }
    }

}
