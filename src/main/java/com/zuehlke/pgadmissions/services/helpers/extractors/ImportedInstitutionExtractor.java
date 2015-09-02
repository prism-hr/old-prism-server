package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.PrismConstants.NULL;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareBooleanForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareColumnsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareIntegerForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedInstitutionImportDTO;

import uk.co.alumeni.prism.api.model.imported.request.ImportedInstitutionRequest;

@Component
public class ImportedInstitutionExtractor<T extends ImportedInstitutionRequest> implements ImportedEntityExtractor<T> {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<T> definitions, boolean enable) {
        List<String> rows = Lists.newLinkedList();
        if (!definitions.isEmpty()) {
            boolean systemImport = definitions.get(0).getClass().equals(ImportedInstitutionImportDTO.class);
            for (ImportedInstitutionRequest definition : definitions) {
                List<String> cells = Lists.newLinkedList();
                cells.add(prepareIntegerForSqlInsert(definition.getDomicile()));
                cells.add(prepareStringForSqlInsert(definition.getName()));

                if (systemImport) {
                    List<Integer> ucasIds = ((ImportedInstitutionImportDTO) definition).getUcasIds();
                    String ucasIdsString = Optional.ofNullable(ucasIds)
                            .map(ids -> ids.stream().map(Objects::toString).collect(Collectors.joining("|")))
                            .orElse(null);
                    cells.add(prepareStringForSqlInsert(ucasIdsString));
                    cells.add(prepareStringForSqlInsert(((ImportedInstitutionImportDTO) definition).getFacebookId()));
                    cells.add(prepareIntegerForSqlInsert(((ImportedInstitutionImportDTO) definition).getHesaId()));
                } else {
                    cells.add(NULL);
                    cells.add(NULL);
                    cells.add(NULL);
                }

                cells.add(prepareBooleanForSqlInsert(false));
                cells.add(prepareBooleanForSqlInsert(enable));
                rows.add(prepareColumnsForSqlInsert(cells));
            }
        }
        return rows;
    }

}
