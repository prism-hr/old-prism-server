package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperStudyOption implements AbstractServiceHelper {

    @Autowired
    private AdvertService advertService;

    @Override
    public void execute() {
        advertService.disableAdvertProgramStudyOptions();
    }

}
