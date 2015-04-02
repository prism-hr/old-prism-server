package com.zuehlke.pgadmissions.services.lifecycle;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.definitions.PrismMaintenanceTask;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.lifecycle.helpers.ImportedEntityServiceHelperSystem;

@Service
public class LifeCycleService {

	private static final Logger logger = LoggerFactory.getLogger(LifeCycleService.class);

	private ExecutorService executorService;

	private Object lock = new Object();

	private Set<PrismMaintenanceTask> executions = Sets.newHashSet();

	@Value("${startup.display.initialize}")
	private Boolean initializeDisplayProperties;

	@Value("${startup.display.initialize.drop}")
	private Boolean dropDisplayProperties;

	@Value("${startup.workflow.initialize}")
	private Boolean initializeWorkflow;

	@Value("${startup.import.system.data}")
	private Boolean initializeData;

	@Value("${maintenance.run}")
	private Boolean maintain;

	@Inject
	private ImportedEntityServiceHelperSystem importedEntityServiceHelperSystem;

	@Inject
	private SystemService systemService;

	@Inject
	private ApplicationContext applicationContext;

	@PostConstruct
	public void startup() {
		try {
			if (BooleanUtils.isTrue(initializeDisplayProperties)) {
				if (BooleanUtils.isTrue(dropDisplayProperties)) {
					systemService.dropDisplayProperties();
				}
				systemService.initializeDisplayProperties();
			}
	
			if (BooleanUtils.isTrue(initializeWorkflow)) {
				systemService.initializeSystem();
			}
	
			if (BooleanUtils.isTrue(initializeData)) {
				importedEntityServiceHelperSystem.execute();
			}
	
			if (BooleanUtils.isTrue(maintain)) {
				executorService = Executors.newFixedThreadPool(PrismMaintenanceTask.values().length);
			}
	} catch (Exception e) {
		logger.error("Error initializing system", e);
		}
	}

	@PreDestroy
	public void shutdown() {
		try {
			executorService.shutdownNow();
		} catch (Exception e) {
			logger.error("Error shutting down maintenance task executor", e);
		}
	}

	@Scheduled(initialDelay = 60, fixedDelay = 60)
	private void maintain() {
		for (final PrismMaintenanceTask execution : PrismMaintenanceTask.values()) {
			synchronized (lock) {
				if (!executions.contains(execution)) {
					executions.add(execution);
					executorService.submit(new Runnable() {
						@Override
						public void run() {
							try {
								applicationContext.getBean(execution.getExecutor()).execute();
							} catch (Exception e) {
								logger.error("Error performing maintenance task: " + execution.name(), e);
							} finally {
								synchronized (lock) {
									executions.remove(execution);
								}
							}
						}
					});
				}
			}
		}
	}

}
