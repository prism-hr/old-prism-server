package com.zuehlke.pgadmissions.timers;

import static org.easymock.EasyMock.expect;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsBlockJUnit4ClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.services.ApplicationExportConfigurationService;

@RunWith(UnitilsBlockJUnit4ClassRunner.class)
public class PorticoThrottleTaskTest {

    @Mock
    @InjectIntoByType
    private ApplicationExportConfigurationService throttleServiceMock;

    @TestedObject
    private PorticoThrottleTask timer;

    @Test
    public void shouldSendApplicationsToQueueService() {
        expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(true);
        expect(throttleServiceMock.getBatchSize()).andReturn(10);

        replay();
        timer.porticoThrottleTask();
    }

    @Test
    public void shouldNotSendApplicationsToQueueService() {
        expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(false);

        replay();
        timer.porticoThrottleTask();
    }

}
