package com.zuehlke.pgadmissions.services.helpers.extractors;

import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareCellsForSqlInsert;
import static com.zuehlke.pgadmissions.utils.PrismQueryUtils.prepareStringForSqlInsert;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismImportedEntity;
import com.zuehlke.pgadmissions.referencedata.jaxb.FundingSources.FundingSource;

@Component
public class ImportedFundingSourceExtractor implements ImportedEntityExtractor {

    @Override
    public List<String> extract(PrismImportedEntity prismImportedEntity, List<Object> definitions) throws Exception {
        List<String> rows = Lists.newLinkedList();
        for (Object definition : definitions) {
            FundingSource fundingSource = (FundingSource) definition;

            List<String> cells = Lists.newLinkedList();
            cells.add(prepareStringForSqlInsert(prismImportedEntity.name()));
            cells.add(prepareStringForSqlInsert(fundingSource.getCode()));
            cells.add(prepareStringForSqlInsert(fundingSource.getName()));
            cells.add(prepareStringForSqlInsert(new Integer(1).toString()));

            String row = prepareCellsForSqlInsert(cells);
            rows.add(row);
        }
        return rows;
    }

}
