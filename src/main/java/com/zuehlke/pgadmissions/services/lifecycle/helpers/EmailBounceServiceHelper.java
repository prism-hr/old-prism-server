package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.EmailBounceService;

@Service
public class EmailBounceServiceHelper implements AbstractServiceHelper {
	
    @Inject
    private EmailBounceService emailBounceService;

    @Override
    public void execute() throws Exception {
        emailBounceService.processEmailBounces();
    }

}
