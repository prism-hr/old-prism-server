package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.jboss.util.Strings.EMPTY;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertListRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationSimple;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class SystemAdvertRecommendationBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) {
        NotificationDefinitionDTO notificationDefinitionDTO = propertyLoader.getNotificationDefinitionDTO();
        AdvertListRepresentation advertListRepresentation = notificationDefinitionDTO.getAdvertListRepresentation();

        List<AdvertRepresentationExtended> advertRepresentations = advertListRepresentation.getRows();
        if (isNotEmpty(advertRepresentations)) {
            List<String> recommendations = Lists.newLinkedList();
            Map<PrismScope, PrismAction> createResourceActions = propertyLoader.getActionService().getCreateResourceActions(APPLICATION);

            for (AdvertRepresentationExtended advertRepresentation : advertListRepresentation.getRows()) {
                ResourceRepresentationSimple resource = advertRepresentation.getResource();

                String title = "<b>" + advertRepresentation.getName() + "</b>";
                String summary = advertRepresentation.getSummary();

                String applyHomepage = advertRepresentation.getApplyHomepage();
                applyHomepage = applyHomepage == null ? propertyLoader.getRedirectionUrl(resource.getId(),
                        createResourceActions.get(resource.getScope()), notificationDefinitionDTO.getRecipient()) : applyHomepage;
                recommendations.add(Joiner.on("<br/>").skipNulls().join(title, summary, propertyLoader.getRedirectionControl(applyHomepage, SYSTEM_APPLY)));
            }

            return "<p>" + Joiner.on("<p></p>").join(recommendations) + "</p>";
        }

        return EMPTY;
    }

}
