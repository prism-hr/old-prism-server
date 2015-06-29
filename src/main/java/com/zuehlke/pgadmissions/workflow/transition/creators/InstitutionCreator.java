package com.zuehlke.pgadmissions.workflow.transition.creators;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private DocumentService documentService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        System system = systemService.getSystem();
        InstitutionDTO newInstitution = (InstitutionDTO) newResource;

        AdvertDTO advertDTO = newInstitution.getAdvert();
        Advert advert = advertService.createAdvert(system, advertDTO);
        Document logoImage = newInstitution.getLogoImage() != null ? documentService.getById(newInstitution.getLogoImage().getId()) : null;

        Institution institution = new Institution().withUser(user).withParentResource(system).withAdvert(advert).withTitle(advert.getTitle())
                .withCurrency(newInstitution.getCurrency()).withBusinessYearStartMonth(newInstitution.getBusinessYearStartMonth())
                .withMinimumWage(newInstitution.getMinimumWage()).withGoogleId(advert.getAddress().getGoogleId()).withUclInstitution(false)
                .withCreatedTimestamp(new DateTime()).withLogoImage(logoImage);
        advert.setInstitution(institution);

        resourceService.setResourceAttributes(institution, newInstitution.getAttributes());
        return institution;
    }

}
