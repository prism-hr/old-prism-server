package com.zuehlke.pgadmissions.controllers;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.QualificationService;

public class DeleteApplicationFormEntitiesControllerTest {

	private ApplicationsService serviceMock;
	private DeleteApplicationFormEntitiesController controller;
	private ApplicationForm applicationForm;
	private QualificationService qualificationServiceMock;
	@Test
	public void shoulGetAddressFromServiceAndDelete(){
		Address address = new Address();
		address.setApplication(applicationForm);
		address.setId(1);
		EasyMock.expect(serviceMock.getAddressById(1)).andReturn(address);
		serviceMock.deleteAddress(address);
		EasyMock.replay(serviceMock);
		controller.deleteAddress(1);
		EasyMock.verify(serviceMock);
	}

	
	@Test
	public void shoulGetQualificationFromServiceAndDelete(){
		Qualification qual = new Qualification();
		qual.setApplication(applicationForm);
		qual.setId(1);
		EasyMock.expect(qualificationServiceMock.getQualificationById(1)).andReturn(qual);
		qualificationServiceMock.delete(qual);
		EasyMock.replay(qualificationServiceMock);
		controller.deleteQualification(1);
		EasyMock.verify(qualificationServiceMock);
	}

	
	@Test
	public void shoulGetFundingFromServiceAndDelete(){
		Funding funding = new Funding();
		funding.setApplication(applicationForm);
		funding.setId(1);
		EasyMock.expect(serviceMock.getFundingById(1)).andReturn(funding);
		serviceMock.deleteFunding(funding);
		EasyMock.replay(serviceMock);
		controller.deleteFunding(1);
		EasyMock.verify(serviceMock);
	}
	
	@Test
	public void shoulGetEmploymentFromServiceAndDelete(){
		EmploymentPosition employment = new EmploymentPosition();
		employment.setApplication(applicationForm);
		employment.setId(1);
		EasyMock.expect(serviceMock.getEmploymentPositionById(1)).andReturn(employment);
		serviceMock.deleteEmployment(employment);
		EasyMock.replay(serviceMock);
		controller.deleteEmployment(1);
		EasyMock.verify(serviceMock);
	}
	
	@Test
	public void shoulGetRefereeFromServiceAndDelete(){
		Referee referee = new Referee();
		referee.setApplication(applicationForm);
		referee.setId(1);
		EasyMock.expect(serviceMock.getRefereeById(1)).andReturn(referee);
		serviceMock.deleteReferee(referee);
		EasyMock.replay(serviceMock);
		controller.deleteReferee(1);
		EasyMock.verify(serviceMock);
	}
	@Before
	public void setup(){
		applicationForm = new ApplicationFormBuilder().id(2).toApplicationForm();
		serviceMock = EasyMock.createMock(ApplicationsService.class);
		qualificationServiceMock = EasyMock.createMock(QualificationService.class);
		controller = new DeleteApplicationFormEntitiesController(serviceMock, qualificationServiceMock);
	}
}
