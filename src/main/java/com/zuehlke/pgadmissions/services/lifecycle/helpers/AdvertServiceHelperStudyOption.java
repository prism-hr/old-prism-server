package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperStudyOption implements AbstractServiceHelper {

	@Inject
	private AdvertService advertService;

	@Override
	public void execute() {
		advertService.disableElapsedAdvertStudyOptions();
	}

}
