package com.zuehlke.pgadmissions.services.exporters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zuehlke.pgadmissions.utils.PausableHibernateCompatibleSequentialTaskExecutor;

//
// We got rid of this implementation for now. Too buggy.
//

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testUclIntegrationContext.xml")
public class PausableHibernateCompatibleSequentialTaskExecutorTest {

    @Autowired
    @Qualifier("ucl-export-service-scheduler")
    private TaskScheduler scheduler;
    
    private PausableHibernateCompatibleSequentialTaskExecutor executor;

    private CountDownLatch latch;
    
    @Before
    public void setup() {
        executor = new PausableHibernateCompatibleSequentialTaskExecutor();
        latch = new CountDownLatch(1);
    }
    
    @Test
    public void shouldPauseFor5Seconds() throws InterruptedException {
        UclExportServiceQueueTest exportServiceQueueTest = new UclExportServiceQueueTest();
        exportServiceQueueTest.pauseWsQueueForMinutes(5);
        assertTrue(executor.isPickingTasksIsPaused());
        while (!latch.await(10, TimeUnit.SECONDS)) {
            // wait
        }
        assertFalse(executor.isPickingTasksIsPaused());
    }
    
    public void completed() {
        latch.countDown();
    }

    class UclExportServiceQueueTest extends UclExportService {
        @Override
        protected void pauseWsQueueForMinutes(int seconds) {
            executor.pause();
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    executor.resume();
                    completed();
                }
            }, DateUtils.addSeconds(new Date(), seconds));
        }
    }
}
