package com.zuehlke.pgadmissions.services;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.ThrottleDAO;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.builders.ThrottleBuilder;

public class ThrottleServiceTest {
	
	private ThrottleService service;
	
	private ThrottleDAO mockRepo;
	
	@Before
	public void setup() {
		mockRepo = createMock(ThrottleDAO.class);
		service = new ThrottleService(mockRepo);
	}
	
	@Test
	public void userTurnedOnThrottleShouldReturnFalse1() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertFalse(service.userTurnedOnThrottle(false));
		verify(mockRepo);
	}
	
	@Test
	public void userTurnedOnThrottleShouldReturnFalse2() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(true).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertFalse(service.userTurnedOnThrottle(false));
		verify(mockRepo);
	}
	
	@Test
	public void userTurnedOnThrottleShouldReturnFalse3() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(true).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertFalse(service.userTurnedOnThrottle(true));
		verify(mockRepo);
	}
	
	@Test
	public void userTurnedOnThrottleShouldReturnTrue() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertTrue(service.userTurnedOnThrottle(true));
		verify(mockRepo);
	}
	
	@Test
	public void shouldUpdateExistingThrottle() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		service.updateThrottleWithNewValues(true, "15");
		
		assertEquals((Boolean) true, throttle.getEnabled());
		assertEquals((Integer) 15, throttle.getBatchSize());
		verify(mockRepo);
	}
	
	@Test(expected = NumberFormatException.class)
	public void shouldNotUpdateExistingThrottleBecauseOfNumberFormat() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		service.updateThrottleWithNewValues(true, "1liv5");
		
		assertEquals((Boolean) false, throttle.getEnabled());
		assertEquals((Integer) 12, throttle.getBatchSize());
		verify(mockRepo);
	}
	
	@Test(expected = NumberFormatException.class)
	public void shouldNotUpdateExistingThrottleWithNegativeBatchSize() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		service.updateThrottleWithNewValues(true, "-15");
		
		verify(mockRepo);
	}
	
	@Test
	public void shouldEnablePorticInterfaceBySettingThrottleOn() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		service.enablePorticoInterface();
		
		assertEquals((Boolean) true, throttle.getEnabled());
		verify(mockRepo);
	}
	
	@Test
	public void shouldDisablePorticInterfaceBySettingThrottleOff() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(true).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		service.disablePorticoInterface();
		
		assertEquals((Boolean) false, throttle.getEnabled());
		verify(mockRepo);
	}
	
	@Test
	public void isPorticoIntefraceEnabledShouldReturnTrue() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(true).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertTrue(service.isPorticoInterfaceEnabled());
		
		verify(mockRepo);
	}
	
	@Test
	public void isPorticoIntefraceEnabledShouldReturnFalse() {
		Throttle throttle = new ThrottleBuilder().batchSize(12).enabled(false).id(1).build();
		expect(mockRepo.get()).andReturn(throttle);
		replay(mockRepo);
		
		assertFalse(service.isPorticoInterfaceEnabled());
		
		verify(mockRepo);
	}
}
