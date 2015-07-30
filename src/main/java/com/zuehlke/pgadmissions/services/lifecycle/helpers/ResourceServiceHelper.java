package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ResourceServiceHelper implements PrismServiceHelper {

	@Inject
	private ResourceService resourceService;

	@Override
	public void execute() {
		resourceService.deleteElapsedStudyOptions();
	}

}
