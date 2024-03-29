package uk.co.alumeni.prism.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.dto.EntityOpportunityCategoryDTO;
import uk.co.alumeni.prism.dto.ResourceOpportunityCategoryDTO;
import uk.co.alumeni.prism.rest.representation.ListSummaryRepresentation;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static java.util.Arrays.stream;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;

public class PrismListUtils {

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Map<String, Integer> summaries) {
        processRowDescriptors(entities, null, summaries);
    }

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Set<Integer> onlyAsPartnerEntityIds,
            Map<String, Integer> summaries) {
        processRowDescriptors(entities, onlyAsPartnerEntityIds, summaries, null);
    }

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Map<String, Integer> summaries,
            List<PrismOpportunityType> opportunityTypes) {
        processRowDescriptors(entities, null, summaries, opportunityTypes);
    }

    public static <T extends EntityOpportunityCategoryDTO> void processRowDescriptors(Set<T> entities, Set<Integer> onlyAsPartnerEntityIds,
            Map<String, Integer> summaries, List<PrismOpportunityType> opportunityTypes) {
        boolean processOnlyAsPartner = false;
        boolean filterByOpportunityType = isNotEmpty(opportunityTypes);

        onlyAsPartnerEntityIds = onlyAsPartnerEntityIds == null ? Sets.newHashSet() : onlyAsPartnerEntityIds;
        for (T entity : entities) {
            Integer entityId = entity.getId();
            processOnlyAsPartner = !processOnlyAsPartner ? entity.getClass().equals(ResourceOpportunityCategoryDTO.class) : processOnlyAsPartner;
            if (processOnlyAsPartner && isTrue(((ResourceOpportunityCategoryDTO) entity).getOnlyAsPartner())) {
                onlyAsPartnerEntityIds.add(entityId);
            }

            String opportunityCategories = entity.getOpportunityCategories();
            if (opportunityCategories != null) {
                for (String opportunityCategory : entity.getOpportunityCategories().split("\\|")) {
                    Integer summary = summaries.get(opportunityCategory);
                    PrismOpportunityType opportunityType = entity.getOpportunityType();
                    if (!filterByOpportunityType || (opportunityType != null && opportunityTypes.contains(opportunityType))) {
                        summaries.put(opportunityCategory, summary == null ? 1 : summary + 1);
                    }
                }
            }
        }

        summaries.put("ALL", entities.size());
    }

    public static List<ListSummaryRepresentation> getSummaryRepresentations(Map<String, Integer> summaries) {
        List<ListSummaryRepresentation> representations = Lists.newArrayList();
        for (Entry<String, Integer> summary : summaries.entrySet()) {
            representations.add(new ListSummaryRepresentation().withOpportunityCategory(summary.getKey()).withRowCount(summary.getValue()));
        }
        return representations;
    }

    public static <T extends EntityOpportunityCategoryDTO> Map<Integer, BigDecimal> getRowsToReturn(Collection<T> entities, PrismOpportunityCategory filterOpportunityCategory,
            Collection<PrismOpportunityType> filterOpportunityTypes, String lastSequenceIdentifier, Integer maxEntities) {
        Integer returned = 0;
        boolean returning = lastSequenceIdentifier == null;

        boolean filteringOpportunityCategory = filterOpportunityCategory != null;
        boolean filteringOpportunityType = isNotEmpty(filterOpportunityTypes);
        String filterOpportunityCategoryName = filteringOpportunityCategory ? filterOpportunityCategory.name() : null;

        Map<Integer, BigDecimal> entityIndex = newLinkedHashMap();
        for (EntityOpportunityCategoryDTO entity : entities) {
            if (returning) {
                boolean included;
                if (!(filteringOpportunityCategory || filteringOpportunityType)) {
                    included = true;
                } else if (filteringOpportunityCategory) {
                    included = stream(entity.getOpportunityCategories().split("\\|")).anyMatch(oc -> oc.equals(filterOpportunityCategoryName));
                } else {
                    included = filterOpportunityTypes.contains(entity.getOpportunityType());
                }

                if (included) {
                    entityIndex.put(entity.getId(), entity.getPriority());
                    returned++;
                }

                if (Objects.equals(returned, maxEntities)) {
                    break;
                }
            } else {
                returning = lastSequenceIdentifier.equals(entity.toString());
            }
        }
        return entityIndex;
    }

}
