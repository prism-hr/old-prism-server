package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.unitils.easymock.EasyMockUnitils.replay;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormDAO;
import com.zuehlke.pgadmissions.dao.FundingDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class FundingServiceTest {

    @Mock
    @InjectIntoByType
    private FundingDAO fundingDAOMock;

    @Mock
    @InjectIntoByType
    ApplicationFormDAO applicationFormDAOMock;

    @Mock
    @InjectIntoByType
    private DocumentService documentServiceMock;
    
    @TestedObject
    private FundingService service;

	@Test			
	public void shouldDelegateGetFundingToDAO(){
		Funding funding = new FundingBuilder().id(2).build();
		
		EasyMock.expect(fundingDAOMock.getFundingById(2)).andReturn(funding);
		
		replay();
		Funding returnedFunding = service.getFundingById(2);
		
		assertEquals(funding, returnedFunding);
	}
	
	@Test			
	public void shouldDelegateDeleteToDAO(){
		Funding funding = new Funding();
		
		fundingDAOMock.delete(funding);
		
		replay();
		service.delete(funding);
	}
	
	@Test			
	public void shouldSaveNewFunding(){
	    Document newDocument = new Document();
	    ApplicationForm applicationForm = new ApplicationForm();
		Funding funding = new FundingBuilder().id(2).document(newDocument).build();
		
		expect(applicationFormDAOMock.get(55)).andReturn(applicationForm);
		documentServiceMock.documentReferentialityChanged(null, newDocument);
		Capture<Funding> fundingCapture = new Capture<Funding>();
		fundingDAOMock.saveOrUpdate(capture(fundingCapture));
		
		replay();
		service.save(55, null, funding);
		
		Funding savedFunding = fundingCapture.getValue();
		assertSame(applicationForm, savedFunding.getApplication());
		assertThat(applicationForm.getFundings(), contains(savedFunding));
	}

	@Test			
	public void shouldUpdateExistingFunding(){
	    Document newDocument = new Document();
	    ApplicationForm applicationForm = new ApplicationFormBuilder().id(55).build();
	    Funding funding = new FundingBuilder().id(66).document(newDocument).application(applicationForm).build();
	    
	    expect(applicationFormDAOMock.get(55)).andReturn(applicationForm);
	    expect(fundingDAOMock.getFundingById(66)).andReturn(funding);
	    documentServiceMock.documentReferentialityChanged(null, newDocument);
	    fundingDAOMock.saveOrUpdate(funding);
	    
	    replay();
	    service.save(55, 66, funding);
	}
	
	
}
