package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class EmailBounceHandler implements AbstractServiceHelper {

    @Inject
    private EmailBounceService emailBounceService;

    @Override
    public void execute() throws Exception {
        emailBounceService.processEmailBounces();
    }

}
