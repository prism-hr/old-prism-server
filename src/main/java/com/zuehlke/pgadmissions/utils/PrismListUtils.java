package com.zuehlke.pgadmissions.utils;

import static org.apache.commons.lang.BooleanUtils.isTrue;

import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.dto.EntityOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.dto.ResourceOpportunityCategoryDTO;
import com.zuehlke.pgadmissions.rest.representation.ListSummaryRepresentation;

public class PrismListUtils {

    public static <T extends EntityOpportunityCategoryDTO> void processRowSummaries(Set<T> entities, HashMultimap<String, Integer> summaries) {
        processRowDescriptors(entities, null, null, summaries);
    }

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Set<Integer> entityIds, Set<Integer> asPartnerEntityIds,
            HashMultimap<String, Integer> summaries) {
        boolean processOnlyAsPartner = false;
        entityIds = entityIds == null ? Sets.newHashSet() : entityIds;
        asPartnerEntityIds = asPartnerEntityIds == null ? Sets.newHashSet() : asPartnerEntityIds;
        for (T entity : entities) {
            Integer entityId = entity.getId();
            entityIds.add(entityId);

            processOnlyAsPartner = !processOnlyAsPartner ? entity.getClass().equals(ResourceOpportunityCategoryDTO.class) : processOnlyAsPartner;
            if (processOnlyAsPartner && isTrue(((ResourceOpportunityCategoryDTO) entity).getOnlyAsPartner())) {
                asPartnerEntityIds.add(entityId);
            }

            String opportunityCategories = entity.getOpportunityCategories();
            if (opportunityCategories != null) {
                for (String opportunityCategory : entity.getOpportunityCategories().split("\\|")) {
                    summaries.put(opportunityCategory, entityId);
                    summaries.put("ALL", entityId);
                }
            }
        }
    }

    public static List<ListSummaryRepresentation> getSummaryRepresentations(HashMultimap<String, Integer> summaries) {
        List<ListSummaryRepresentation> representations = Lists.newArrayList();
        summaries.keySet().forEach(summary -> {
            representations.add(new ListSummaryRepresentation().withOpportunityCategory(summary).withRowCount(summaries.get(summary).size()));
        });
        return representations;
    }

}
