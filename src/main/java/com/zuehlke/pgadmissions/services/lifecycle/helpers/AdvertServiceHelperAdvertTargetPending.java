package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperAdvertTargetPending extends PrismServiceHelperAbstract {

    @Inject
    private AdvertService advertService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        advertService.getAdvertTargetPendings().forEach(advertTargetPending -> {
            processAdvertTargetPending(advertTargetPending);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }
    
    private void processAdvertTargetPending(Integer advertTargetPending) {
        if (!isShuttingDown()) {
            advertService.processAdvertTargetPending(advertTargetPending);
        }
    }

}
