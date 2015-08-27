package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperStudyOption extends PrismServiceHelperAbstract {

    @Inject
    private AdvertService advertService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        deleteElapsedStudyOptions();
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void deleteElapsedStudyOptions() {
        if (!isShuttingDown()) {
            advertService.deleteElapsedAdvertStudyOptions();
        }
    }

}
