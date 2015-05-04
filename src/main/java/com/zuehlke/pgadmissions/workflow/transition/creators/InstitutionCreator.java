package com.zuehlke.pgadmissions.workflow.transition.creators;

import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        InstitutionDTO newInstitution = (InstitutionDTO) newResource;

        AdvertDTO advertDTO = newInstitution.getAdvert();
        Advert advert = advertService.createAdvert(user, advertDTO);

        Institution institution = new Institution().withUser(user).withParentResource(systemService.getSystem())
                .withDomicile(advert.getAddress().getDomicile()).withAdvert(advert).withTitle(advert.getTitle()).withCurrency(newInstitution.getCurrency())
                .withBusinessYearStartMonth(newInstitution.getBusinessYearStartMonth()).withGoogleId(advert.getAddress().getLocation().getGoogleId())
                .withUclInstitution(false).withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD)).withCreatedTimestamp(new DateTime());
        advert.setInstitution(institution);

        resourceService.setAttributes(institution, newInstitution.getAttributes());
        return institution;
    }

}
