package com.zuehlke.pgadmissions.timers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.jms.PorticoQueueService;
import com.zuehlke.pgadmissions.services.ThrottleService;

public class PorticoThrottleTaskTest {
	
	private ThrottleService throttleServiceMock;
	private PorticoQueueService porticoQueueServiceMock;
	private PorticoThrottleTask timer;
	
	@Before
	public void setup() {
		throttleServiceMock = createMock(ThrottleService.class);
		porticoQueueServiceMock = createMock(PorticoQueueService.class);
		timer = new PorticoThrottleTask(throttleServiceMock, porticoQueueServiceMock);
	}
	
	@Test
	public void shouldSendApplicationsToQueueService() {
		expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(true);
		expect(throttleServiceMock.getBatchSize()).andReturn(10);
		porticoQueueServiceMock.sendQueuedWithdrawnOrRejectedApplicationsToPortico(10);
		replay(throttleServiceMock, porticoQueueServiceMock);
		
		timer.porticoThrottleTask();
		
		verify(throttleServiceMock, porticoQueueServiceMock);
	}
	
	@Test
	public void shouldNotSendApplicationsToQueueService() {
		expect(throttleServiceMock.isPorticoInterfaceEnabled()).andReturn(false);
		replay(throttleServiceMock, porticoQueueServiceMock);
		
		timer.porticoThrottleTask();
		
		verify(throttleServiceMock, porticoQueueServiceMock);
	}

}
