package com.zuehlke.pgadmissions.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.rest.representation.ListSummaryRepresentation;

public class PrismListUtils {

    public static <T extends EntityOpportunityCategoryDTO> void processRowSummaries(Set<T> entities, Map<String, Integer> summaries) {
        processRowDescriptors(entities, null, summaries);
    }

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Set<Integer> entityIds, Map<String, Integer> summaries) {
        entityIds = entityIds == null ? Sets.newHashSet() : entityIds;
        for (T entity : entities) {
            entityIds.add(entity.getId());
            String opportunityCategories = entity.getOpportunityCategories();
            if (opportunityCategories != null) {
                for (String opportunityCategory : entity.getOpportunityCategories().split("\\|")) {
                    Integer summary = summaries.get(opportunityCategory);
                    summaries.put(opportunityCategory, summary == null ? 1 : summary + 1);
                }
            }
        }

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
