package com.zuehlke.pgadmissions.controllers.applicantform;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
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
	private EncryptionHelper encryptionHelperMock;


	
	@Test
	public void shoulGetQualificationFromServiceAndDelete(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		Qualification qual = new QualificationBuilder().application(applicationForm).id(1).build();
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qual);
		qualificationServiceMock.delete(qual);
		EasyMock.replay(qualificationServiceMock, encryptionHelperMock);
		String viewName = controller.deleteQualification("encryptedId");		
		EasyMock.verify(qualificationServiceMock);
		assertEquals("redirect:/update/getQualification?applicationId=2&message=deleted",viewName);
	}

	@Test
	public void shoulGetFundingFromServiceAndDelete(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		Funding funding = new FundingBuilder().id(1).application(applicationForm).build();
		
		EasyMock.expect(fundingServiceMock.getFundingById(1)).andReturn(funding);
		fundingServiceMock.delete(funding);
		EasyMock.replay(fundingServiceMock, encryptionHelperMock);
		String viewName = controller.deleteFunding("encryptedId");
		EasyMock.verify(fundingServiceMock, encryptionHelperMock);
		assertEquals("redirect:/update/getFunding?applicationId=2&message=deleted",viewName);
	}
	
	@Test
	public void shoulGetEmploymentFromServiceAndDelete(){
		EasyMock.expect(encryptionHelperMock.decryptToInteger("encryptedId")).andReturn(1);
		
		EmploymentPosition employment = new EmploymentPosition();
		employment.setApplication(applicationForm);
		employment.setId(1);
		EasyMock.expect(employmentServiceMock.getEmploymentPositionById(1)).andReturn(employment);
		employmentServiceMock.delete(employment);
		EasyMock.replay(employmentServiceMock, encryptionHelperMock);
		
		String viewName = controller.deleteEmployment("encryptedId");
		
		EasyMock.verify(employmentServiceMock);
		assertEquals("redirect:/update/getEmploymentPosition?applicationId=2&message=deleted",viewName);
	}
	
	@Test
	public void shoulGetRefereeFromServiceAndDelete(){
		Referee referee = new Referee();
		referee.setApplication(applicationForm);
		referee.setId(1);

		EasyMock.expect(encryptionHelperMock.decryptToInteger("lala")).andReturn(123);
		EasyMock.expect(refereeServiceMock.getRefereeById(123)).andReturn(referee);
		refereeServiceMock.delete(referee);
		EasyMock.replay(refereeServiceMock, encryptionHelperMock);
		
		String viewName = controller.deleteReferee("lala");
		
		EasyMock.verify(refereeServiceMock, encryptionHelperMock);
		assertEquals("redirect:/update/getReferee?applicationId=2&message=deleted",viewName);
	}
	@Before
	public void setup(){
		applicationForm = new ApplicationFormBuilder().id(2).applicationNumber("2").build();
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);
		employmentServiceMock = EasyMock.createMock(EmploymentPositionService.class);
		fundingServiceMock = EasyMock.createMock(FundingService.class);
		refereeServiceMock = EasyMock.createMock(RefereeService.class);
		encryptionHelperMock = EasyMock.createMock(EncryptionHelper.class);
		controller = new DeleteApplicationFormEntitiesController(qualificationServiceMock, employmentServiceMock, fundingServiceMock, refereeServiceMock, encryptionHelperMock);
	}
}
