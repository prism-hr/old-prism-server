package com.zuehlke.pgadmissions.controllers;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.services.OpportunitiesService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class RequestsControllerTest {

	@Mock
	@InjectIntoByType
	private OpportunitiesService opportunitiesService;

	@Mock
	@InjectIntoByType
	private UserService userService;

	@TestedObject
	private RequestsController controller = new RequestsController();

	@Test
	public void shouldGetRequestsPage() {
		String result = controller.getRequestsPage();
		assertEquals(RequestsController.REQUESTS_PAGE_VIEW_NAME, result);
	}

	@Test
	public void shouldGetOpportunityRequests() {
		ArrayList<OpportunityRequest> opportunityRequests = Lists.newArrayList();
		expect(opportunitiesService.getInitialOpportunityRequests()).andReturn(opportunityRequests);

		replay();
		List<OpportunityRequest> returned = controller.getOpportunityRequests();
		verify();

		assertSame(opportunityRequests, returned);
	}

}
