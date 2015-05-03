package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.ResourceDTO;
import com.zuehlke.pgadmissions.services.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.zuehlke.pgadmissions.utils.PrismConstants.ADVERT_TRIAL_PERIOD;

@Component
public class InstitutionCreator implements ResourceCreator {

    @Inject
    private AdvertService advertService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private EntityService entityService;

    @Inject
    private SystemService systemService;

    @Override
    public Resource create(User user, ResourceDTO newResource) throws Exception {
        InstitutionDTO newInstitution = (InstitutionDTO) newResource;

        AdvertDTO advertDTO = newInstitution.getAdvert();
        Advert advert = advertService.createAdvert(user, advertDTO);

        Institution institution = new Institution().withUser(user).withSystem(systemService.getSystem()).withDomicile(advert.getAddress().getDomicile())
                .withAdvert(advert).withTitle(advert.getTitle()).withCurrency(newInstitution.getCurrency())
                .withBusinessYearStartMonth(newInstitution.getBusinessYearStartMonth()).withGoogleId(newInstitution.getGoogleIdentifier())
                .withUclInstitution(false).withEndDate(new LocalDate().plusMonths(ADVERT_TRIAL_PERIOD)).withCreatedTimestamp(new DateTime());
        advert.setInstitution(institution);
        entityService.save(institution);

        institutionService.setInstitutionImages(newInstitution, institution);
        resourceService.setAttributes(institution, newInstitution.getAttributes());
        return institution;
    }

}
