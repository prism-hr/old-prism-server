package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;

public class FundingServiceTest {
	private FundingDAO fundingDAOMock;
	private FundingService fundingService;

	@Test			
	public void shouldDelegateGetFundingToDAO(){
		Funding funding = new FundingBuilder().id(2).build();
		EasyMock.expect(fundingDAOMock.getFundingById(2)).andReturn(funding);
		EasyMock.replay(fundingDAOMock);
		Funding returnedFunding = fundingService.getFundingById(2);
		assertEquals(funding, returnedFunding);
	}
	
	@Test			
	public void shouldDelegateDeleteToDAO(){
		Funding funding = new FundingBuilder().id(2).build();
		fundingDAOMock.delete(funding);
		EasyMock.replay(fundingDAOMock);
		fundingService.delete(funding);
		EasyMock.verify(fundingDAOMock);
	
	}
	
	@Test			
	public void shouldDelegateSaveToDAO(){
		Funding funding = new FundingBuilder().id(2).build();
		fundingDAOMock.save(funding);
		EasyMock.replay(fundingDAOMock);
		fundingService.save(funding);
		EasyMock.verify(fundingDAOMock);
	
	}
	
	@Before
	public void setup(){
		fundingDAOMock = EasyMock.createMock(FundingDAO.class);
		fundingService = new FundingService(fundingDAOMock);
	}
}
