package com.zuehlke.pgadmissions.timers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.services.PorticoQueueService;
import com.zuehlke.pgadmissions.services.ThrottleService;

@Service
public class PorticoThrottleTask {

    private final Logger log = LoggerFactory.getLogger(PorticoThrottleTask.class);
    
    private final ThrottleService throttleService;

    private PorticoQueueService queueService;
    
    public PorticoThrottleTask() {
        this(null, null);
    }
    
    @Autowired
    public PorticoThrottleTask(final ThrottleService throttleService, final PorticoQueueService queueService) {
        this.throttleService = throttleService;
        this.queueService = queueService;
    }

    @Scheduled(cron = "${xml.data.export.throttle.cron}")
    public void porticoThrottleTask() {
        log.info("Portico Throttle Task Running");
        if (throttleService.isPorticoInterfaceEnabled()) {
            int batchSize = throttleService.getBatchSize();
            log.info(String.format("Throttle is set to send %d applications to PORTICO", batchSize));
            queueService.sendApplicationsToBeSentToPortico(batchSize);
        } else {
            log.info("Portico interface is disabled");
        }
        log.info("Portico Throttle Task Completed");
    }
}