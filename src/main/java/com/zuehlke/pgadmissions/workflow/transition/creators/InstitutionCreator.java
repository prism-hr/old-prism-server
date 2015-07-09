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
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

    @Inject
    private AdvertService advertService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, InstitutionDTO newResource) throws Exception {
        System system = systemService.getSystem();

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(system, advertDTO);

        FileDTO logoImageDTO = newResource.getLogoImage();
        Document logoImage = logoImageDTO == null ? null : documentService.getById(logoImageDTO.getId());

        Institution institution = new Institution().withUser(user).withParentResource(system).withAdvert(advert).withTitle(advert.getTitle())
                .withCurrency(newResource.getCurrency()).withBusinessYearStartMonth(newResource.getBusinessYearStartMonth())
                .withMinimumWage(newResource.getMinimumWage()).withGoogleId(advert.getAddress().getGoogleId()).withUclInstitution(false)
                .withCreatedTimestamp(new DateTime()).withLogoImage(logoImage);
        advert.setInstitution(institution);

        resourceService.setResourceAttributes(institution, newResource);
        return institution;
    }

}
