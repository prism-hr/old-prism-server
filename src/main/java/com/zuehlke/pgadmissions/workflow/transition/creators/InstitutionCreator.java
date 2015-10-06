package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.PrismConstants.SYSTEM_CURRENCY;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityCategory;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PrismScopeCreationDefault;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.PrismService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private PrismService prismService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, InstitutionDTO newResource) {
        System system = systemService.getSystem();

        AdvertDTO advertDTO = newResource.getAdvert();
        advertDTO.setGloballyVisible(INSTITUTION.isDefaultShared());
        Advert advert = advertService.createAdvert(system, advertDTO, newResource.getName(), user);

        String currency = newResource.getCurrency();
        if (currency == null) {
            Domicile domicile = prismService.getDomicileById(advertDTO.getAddress().getDomicile());
            currency = domicile == null ? SYSTEM_CURRENCY : domicile.getCurrency();
        }

        PrismScopeCreationDefault creationDefault = INSTITUTION.getDefault(newResource.getContext());
        Integer businessYearStartMonth = newResource.getBusinessYearStartMonth();
        businessYearStartMonth = businessYearStartMonth == null ? creationDefault.getDefaultBusinessYearStartMonth().getValue() : businessYearStartMonth;

        Institution institution = new Institution().withParentResource(system).withImportedCode(newResource.getImportedCode()).withUser(user).withAdvert(advert)
                .withName(advert.getName()).withCurrency(currency).withBusinessYearStartMonth(businessYearStartMonth)
                .withGoogleId(advert.getAddress().getGoogleId());

        List<PrismOpportunityCategory> opportunityCategories = newResource.getOpportunityCategories();
        newResource.setOpportunityCategories(isEmpty(opportunityCategories) ? asList(creationDefault.getDefaultOpportunityCategories()) : opportunityCategories);

        resourceService.setResourceAttributes(institution, newResource);
        return institution;
    }

}
