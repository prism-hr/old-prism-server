package com.zuehlke.pgadmissions.services.lifecycle;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.definitions.PrismMaintenanceTask;

@Service
public class MaintenanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceService.class);

	@Value("${maintenance.run}")
	private Boolean maintenanceRun;

	@Inject
	private AbstractApplicationContext applicationContext;

	@Resource(name = "taskExecutor")
	private ThreadPoolTaskExecutor executor;

	@Scheduled(initialDelay = 60000, fixedDelay = 60000)
	public void maintain() {
		if (BooleanUtils.isTrue(maintenanceRun)) {
			for (PrismMaintenanceTask task : PrismMaintenanceTask.values()) {
				if (task.isExecute()) {
					if (task.isParallelize()) {
						submit(task);
					} else {
						execute(task);
					}
				}
			}
		}
	}

	private void submit(final PrismMaintenanceTask task) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				execute(task);

			}
		});
	}

	private void execute(final PrismMaintenanceTask task) {
		try {
			if (applicationContext.isActive()) {
				applicationContext.getBean(task.getExecutor()).execute();
			}
		} catch (Exception e) {
			LOGGER.error("Error performing maintenance task", e);
		}
	}

}
