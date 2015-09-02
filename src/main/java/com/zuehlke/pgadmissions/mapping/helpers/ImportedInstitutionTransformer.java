package com.zuehlke.pgadmissions.mapping.helpers;

import com.zuehlke.pgadmissions.domain.imported.ImportedEntitySimple;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import org.springframework.stereotype.Component;
import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ImportedInstitutionTransformer<T extends ImportedInstitutionRequest> implements ImportedEntityTransformer<T, ImportedInstitution> {

    @Inject
    private ImportedEntityService importedEntityService;

    @Override
    public void transform(T concreteSource, ImportedInstitution concreteTarget) {
        concreteTarget.setDomicile((ImportedEntitySimple) importedEntityService.getById(ImportedEntitySimple.class, concreteSource.getDomicile()));
        if (concreteSource.getClass().equals(ImportedInstitutionImportDTO.class)) {
            List<Integer> ucasIds = ((ImportedInstitutionImportDTO) concreteSource).getUcasIds();
            String ucasId = Optional.ofNullable(ucasIds)
                    .map(ids -> ids.stream().map(Objects::toString).collect(Collectors.joining("|")))
                    .orElse(null);
            concreteTarget.setUcasIds(ucasId);
            concreteTarget.setFacebookId(((ImportedInstitutionImportDTO) concreteSource).getFacebookId());
            concreteTarget.setHesaId(((ImportedInstitutionImportDTO) concreteSource).getHesaId());
        }
    }

}
