package com.zuehlke.pgadmissions.workflow.transition.creators;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.InstitutionDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.ImportedEntityService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

	@Inject
	private AdvertService advertService;

	@Inject
	private ResourceService resourceService;

	@Inject
	private ImportedEntityService importedEntityService;

	@Inject
	private SystemService systemService;

	@Override
	public Resource create(User user, InstitutionDTO newResource) {
		System system = systemService.getSystem();
		AdvertDTO advertDTO = newResource.getAdvert();
		Advert advert = advertService.createAdvert(system, advertDTO, newResource.getName(), user);

		ImportedInstitution importedInstitution = Optional.ofNullable(newResource.getImportedInstitutionId())
				.map(id -> importedEntityService.getById(ImportedInstitution.class, id)).orElse(null);

		Institution institution = new Institution().withUser(user).withParentResource(system).withAdvert(advert)
				.withName(advert.getName()).withCurrency(newResource.getCurrency())
				.withBusinessYearStartMonth(newResource.getBusinessYearStartMonth())
				.withGoogleId(advert.getAddress().getGoogleId()).withUclInstitution(false)
				.withImportedInstitution(importedInstitution);

		resourceService.setResourceAttributes(institution, newResource);
		return institution;
	}

}
