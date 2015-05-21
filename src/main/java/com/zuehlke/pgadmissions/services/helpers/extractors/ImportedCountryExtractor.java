package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.referencedata.jaxb.Countries.Country;

@Component
public class ImportedCountryExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(Institution institution, PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            Country country = (Country) definition;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(institution.getId().toString()));
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert(country.getCode()));
            cells.add(prepareStringForSqlInsert(country.getName()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));

            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
