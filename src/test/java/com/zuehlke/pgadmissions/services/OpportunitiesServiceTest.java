package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.OpportunityRequestDAO;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class OpportunitiesServiceTest {

	@Mock
	@InjectIntoByType
	private RegistrationService registrationService;

	@Mock
	@InjectIntoByType
	private OpportunityRequestDAO opportunityRequestDAO;

	@TestedObject
	private OpportunitiesService service = new OpportunitiesService();

	@Test
	public void test() {
		RegisteredUser author = new RegisteredUser();
		OpportunityRequest opportunityRequest = new OpportunityRequestBuilder().author(author).build();

		expect(registrationService.updateOrSaveUser(author, null)).andReturn(null);
		opportunityRequestDAO.save(opportunityRequest);

		replay();
		service.createOpportunityRequestAndAuthor(opportunityRequest);
		verify();
	}

}
