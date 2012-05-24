package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.controllers.applicantform.DeleteApplicationFormEntitiesController;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.services.EmploymentPositionService;
import com.zuehlke.pgadmissions.services.FundingService;
import com.zuehlke.pgadmissions.services.QualificationService;
import com.zuehlke.pgadmissions.services.RefereeService;

public class DeleteApplicationFormEntitiesControllerTest {

	
	private DeleteApplicationFormEntitiesController controller;
	private ApplicationForm applicationForm;
	private QualificationService qualificationServiceMock;
	private EmploymentPositionService employmentServiceMock;
	private FundingService fundingServiceMock;
	private RefereeService refereeServiceMock;


	
	@Test
	public void shoulGetQualificationFromServiceAndDelete(){
		Qualification qual = new Qualification();
		qual.setApplication(applicationForm);
		qual.setId(1);
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qual);
		qualificationServiceMock.delete(qual);
		EasyMock.replay(qualificationServiceMock);
		String viewName = controller.deleteQualification(1);		
		EasyMock.verify(qualificationServiceMock);
		assertEquals("redirect:/update/getQualification?applicationId=2&message=deleted",viewName);
	}

	@Test
	public void shoulGetFundingFromServiceAndDelete(){
		Funding funding = new FundingBuilder().id(1).application(applicationForm).toFunding();
		
		EasyMock.expect(fundingServiceMock.getFundingById(1)).andReturn(funding);
		fundingServiceMock.delete(funding);
		EasyMock.replay(fundingServiceMock);
		String viewName = controller.deleteFunding(1);
		EasyMock.verify(fundingServiceMock);
		assertEquals("redirect:/update/getFunding?applicationId=2&message=deleted",viewName);
	}
	
	@Test
	public void shoulGetEmploymentFromServiceAndDelete(){
		EmploymentPosition employment = new EmploymentPosition();
		employment.setApplication(applicationForm);
		employment.setId(1);
		EasyMock.expect(employmentServiceMock.getEmploymentPositionById(1)).andReturn(employment);
		employmentServiceMock.delete(employment);
		EasyMock.replay(employmentServiceMock);
		String viewName = controller.deleteEmployment(1);
		EasyMock.verify(employmentServiceMock);
		assertEquals("redirect:/update/getEmploymentPosition?applicationId=2&message=deleted",viewName);
	}
	
	@Test
	public void shoulGetRefereeFromServiceAndDelete(){
		Referee referee = new Referee();
		referee.setApplication(applicationForm);
		referee.setId(1);
		EasyMock.expect(refereeServiceMock.getRefereeById(1)).andReturn(referee);
		refereeServiceMock.delete(referee);
		EasyMock.replay(refereeServiceMock);
		String viewName = controller.deleteReferee(1);
		EasyMock.verify(refereeServiceMock);
		assertEquals("redirect:/update/getReferee?applicationId=2&message=deleted",viewName);
	}
	@Before
	public void setup(){
		applicationForm = new ApplicationFormBuilder().id(2).toApplicationForm();
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);
		employmentServiceMock = EasyMock.createMock(EmploymentPositionService.class);
		fundingServiceMock = EasyMock.createMock(FundingService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		controller = new DeleteApplicationFormEntitiesController(qualificationServiceMock, employmentServiceMock, fundingServiceMock, refereeServiceMock);
	}
}
