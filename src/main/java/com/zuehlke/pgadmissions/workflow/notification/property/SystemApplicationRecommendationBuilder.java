package com.zuehlke.pgadmissions.workflow.notification.property;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_NO_RECOMMENDATIONS;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.helpers.NotificationPropertyLoader;

@Component
public class SystemApplicationRecommendationBuilder implements NotificationPropertyBuilder {

    @Override
    public String build(NotificationPropertyLoader propertyLoader) throws Exception {
        NotificationDefinitionModelDTO modelDTO = propertyLoader.getNotificationDefinitionModelDTO();
        List<AdvertRecommendationDTO> advertRecommendations = modelDTO.getAdvertRecommendations();

        if (!advertRecommendations.isEmpty()) {
            List<String> recommendations = Lists.newLinkedList();
            Map<PrismScope, PrismAction> createResourceActions = propertyLoader.getActionService().getCreateResourceActions(APPLICATION);

            for (AdvertRecommendationDTO advertRecommendation : advertRecommendations) {
                Advert advert = advertRecommendation.getAdvert();
                ResourceParent resourceParent = advert.getResource();

                String title = "<b>" + advert.getName() + "</b>";
                String summary = advert.getSummary();

                String applyHomepage = advert.getApplyHomepage();
                applyHomepage = applyHomepage == null ? propertyLoader.buildRedirectionUrl(resourceParent,
                        createResourceActions.get(resourceParent.getResourceScope()), modelDTO.getUser()) : applyHomepage;
                recommendations.add(Joiner.on("<br/>").skipNulls().join(title, summary, propertyLoader.buildRedirectionControl(applyHomepage, SYSTEM_APPLY)));
            }

            return "<p>" + Joiner.on("<p></p>").join(recommendations) + "</p>";
        }

        return propertyLoader.getPropertyLoader().loadLazy(SYSTEM_NOTIFICATION_NO_RECOMMENDATIONS);
    }

}
