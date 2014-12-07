package com.zuehlke.pgadmissions.services.lifecycle;

import javax.annotation.Resource;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.MaintenanceTask;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

@Service
public class MaintenanceService {
    
    @Value("${maintenance.run}")
    private Boolean maintenanceRun;
    
    @Autowired
    private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

    @Autowired
    private SystemService systemService;

    @Autowired
    private AbstractApplicationContext applicationContext;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    public void maintain() {
        if (BooleanUtils.isTrue(maintenanceRun)) {
            for (MaintenanceTask task : MaintenanceTask.values()) {
                if (task.isParallelize()) {
                    submit(task);
                } else {
                    execute(task);
                }
            }  
        }
    }

    private void submit(final MaintenanceTask task) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                execute(task);
            }
        });
    }

    private void execute(final MaintenanceTask task) {
        try {
            if (applicationContext.isActive()) {
                applicationContext.getBean(task.getExecutor()).execute();
            }
        } catch (BeansException e) {
            throw new Error(e);
        } catch (Exception e) {
        }
    }

}
