package uk.co.alumeni.prism.workflow.transition.creators;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static uk.co.alumeni.prism.PrismConstants.SYSTEM_CURRENCY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.advert.Advert;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.PrismScopeCreationDefault;
import uk.co.alumeni.prism.domain.resource.Institution;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionDTO;
import uk.co.alumeni.prism.services.AdvertService;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.PrismService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private DocumentService documentService;

    @Inject
    private PrismService prismService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, InstitutionDTO newResource) {
        System system = systemService.getSystem();

        Advert advert = advertService.createResourceAdvert(newResource, system, user);


        String currency = newResource.getCurrency();
        if (currency == null) {
            Domicile domicile = prismService.getDomicileById(newResource.getAddress().getDomicile());
            currency = domicile == null ? SYSTEM_CURRENCY : domicile.getCurrency();
        }

        PrismScopeCreationDefault creationDefault = INSTITUTION.getDefault(newResource.getContext());
        Integer businessYearStartMonth = newResource.getBusinessYearStartMonth();
        businessYearStartMonth = businessYearStartMonth == null ? creationDefault.getDefaultBusinessYearStartMonth().getValue() : businessYearStartMonth;

        Institution institution = new Institution().withParentResource(system).withImportedCode(newResource.getImportedCode()).withUser(user).withAdvert(advert)
                .withName(advert.getName()).withCurrency(currency).withBusinessYearStartMonth(businessYearStartMonth)
                .withGoogleId(advert.getAddress().getGoogleId());

        DocumentDTO logoImageDTO = newResource.getLogoImage();
        if (logoImageDTO != null) {
            institution.setLogoImage(documentService.getById(logoImageDTO.getId()));
        }

        List<PrismOpportunityCategory> opportunityCategories = newResource.getOpportunityCategories();
        newResource.setOpportunityCategories(isEmpty(opportunityCategories) ? asList(creationDefault.getDefaultOpportunityCategories()) : opportunityCategories);

        resourceService.setResourceAttributes(institution, newResource);
        return institution;
    }

}
