package com.zuehlke.pgadmissions.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.rest.representation.ListSummaryRepresentation;

public class PrismListUtils {

    public static void processRowDescriptors(Set<EntityOpportunityCategoryDTO> entities, Set<Integer> entityIds, Map<String, Integer> summaries) {
        entities.forEach(entity -> {
            entityIds.add(entity.getId());
            String opportunityCategories = entity.getOpportunityCategories();
            if (opportunityCategories != null) {
                for (String opportunityCategory : entity.getOpportunityCategories().split("\\|")) {
                    Integer summary = summaries.get(opportunityCategory);
                    summaries.put(opportunityCategory, summary == null ? 1 : summary + 1);
                }
            }
        });

        summaries.put("ALL", entityIds.size());
    }

    public static List<ListSummaryRepresentation> getSummaryRepresentations(Map<String, Integer> summaries) {
        List<ListSummaryRepresentation> representations = Lists.newArrayList();
        for (Entry<String, Integer> summary : summaries.entrySet()) {
            representations.add(new ListSummaryRepresentation().withOpportunityCategory(summary.getKey()).withRowCount(summary.getValue()));
        }
        return representations;
    }

}
