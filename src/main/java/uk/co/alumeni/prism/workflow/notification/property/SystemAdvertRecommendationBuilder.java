package uk.co.alumeni.prism.workflow.notification.property;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.jboss.util.Strings.EMPTY;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertListRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.services.helpers.NotificationPropertyLoader;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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
                recommendations.add("<div class='position'>"+Joiner.on("<br/>").skipNulls().join(title, summary, propertyLoader.getRedirectionControl(applyHomepage, SYSTEM_APPLY))+"</div>");
            }

            return "<p>" + Joiner.on(" ").join(recommendations) + "</p>";
        }

        return EMPTY;
    }

}
