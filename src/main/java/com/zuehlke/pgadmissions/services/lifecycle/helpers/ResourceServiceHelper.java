package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ResourceServiceHelper extends PrismServiceHelperAbstract {

    @Inject
    private ResourceService resourceService;
    
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
            resourceService.deleteElapsedStudyOptions();
        }
    }
    
}
