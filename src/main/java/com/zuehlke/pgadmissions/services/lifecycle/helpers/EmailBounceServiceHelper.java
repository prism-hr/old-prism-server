package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.EmailBounceService;

@Service
public class EmailBounceServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private EmailBounceService emailBounceService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        processEmailBounces();
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }
    
    private void processEmailBounces() {
        if (!isShuttingDown()) {
            emailBounceService.processEmailBounces();
        }
    }

}
