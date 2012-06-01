package com.zuehlke.pgadmissions.services;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.QualificationDAO;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.builders.QualificationBuilder;

public class QualificationServiceTest {

	private QualificationDAO qualificationDAOMock;
	private QualificationService qualificationService;

	@Test			
	public void shouldDelegateGetQualificationToDAO(){
		Qualification qualification = new QualificationBuilder().id(2).toQualification();
		EasyMock.expect(qualificationDAOMock.getQualificationById(2)).andReturn(qualification);
		EasyMock.replay(qualificationDAOMock);
		Qualification returnedQualification = qualificationService.getQualificationById(2);
		assertEquals(qualification, returnedQualification);
	}
	
	@Test			
	public void shouldDelegateDeleteToDAO(){
		Qualification qualification = new QualificationBuilder().id(2).toQualification();
		qualificationDAOMock.delete(qualification);
		EasyMock.replay(qualificationDAOMock);
		qualificationService.delete(qualification);
		EasyMock.verify(qualificationDAOMock);
	
	}
	
	@Test			
	public void shouldDelegateSaveToDAO(){
		Qualification qualification = new QualificationBuilder().id(2).toQualification();
		qualificationDAOMock.save(qualification);
		EasyMock.replay(qualificationDAOMock);
		qualificationService.save(qualification);
		EasyMock.verify(qualificationDAOMock);
	
	}
	
	@Before
	public void setup(){
		qualificationDAOMock = EasyMock.createMock(QualificationDAO.class);
		qualificationService = new QualificationService(qualificationDAOMock);
	}
}
