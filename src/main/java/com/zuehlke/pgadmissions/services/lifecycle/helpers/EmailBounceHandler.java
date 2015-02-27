package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class EmailBounceHandler implements AbstractServiceHelper {

    @Inject
    private EmailBounceService emailBounceService;

    @Override
    public void execute() throws Exception {
        emailBounceService.processEmailBounces();
    }

}
