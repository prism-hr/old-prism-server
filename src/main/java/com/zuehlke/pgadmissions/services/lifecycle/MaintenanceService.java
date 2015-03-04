package com.zuehlke.pgadmissions.services.lifecycle;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.PrismMaintenanceTask;

@Service
public class MaintenanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceService.class);

	private final Object lock = new Object();
	
	private PrismMaintenanceTask blockingTask;

	@Value("${maintenance.run}")
	private Boolean maintenanceRun;

	@Inject
	private AbstractApplicationContext applicationContext;

	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor executor;

	@Scheduled(initialDelay = 60000, fixedDelay = 60000)
	public void maintain() {
		if (BooleanUtils.isTrue(maintenanceRun)) {
			for (PrismMaintenanceTask prismMaintenanceTask : PrismMaintenanceTask.values()) {
				if (prismMaintenanceTask.isExecute()) {
					if (!prismMaintenanceTask.isBlocking()) {
						submit(prismMaintenanceTask);
					} else synchronized (lock) {
						if (blockingTask == null) {
							execute(prismMaintenanceTask);
						}
					}
				}
			}
		}
	}

	private void submit(final PrismMaintenanceTask prismMaintenanceTask) {
		try {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					execute(prismMaintenanceTask);
	
				}
			});
		} catch (TaskRejectedException e) {
			LOGGER.info("Error scheduling maintenance task");
		} catch (Exception e) {
			LOGGER.error("Error scheduling maintenance task", e);			
		}
	}

	private void execute(final PrismMaintenanceTask prismMaintenanceTask) {
		try {
			if (applicationContext.isActive()) {
				applicationContext.getBean(prismMaintenanceTask.getExecutor()).execute();
			}
		} catch (Exception e) {
			LOGGER.error("Error performing maintenance task", e);
		} finally {
			if (prismMaintenanceTask.isBlocking()) synchronized (lock) {
				blockingTask = null;
			}
		}
	}

}
