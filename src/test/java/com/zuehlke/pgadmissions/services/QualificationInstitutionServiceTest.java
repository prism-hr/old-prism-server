package com.zuehlke.pgadmissions.services;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.unitils.easymock.EasyMockUnitils.replay;
import static org.unitils.easymock.EasyMockUnitils.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.QualificationInstitutionDAO;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class QualificationInstitutionServiceTest {

	@Mock
	@InjectIntoByType
	private QualificationInstitutionDAO qualificationInstitutionDAO;
	
	@TestedObject
	private QualificationInstitutionService service = new QualificationInstitutionService();
	
	@Test
	public void shouldGetInstitutionByCode() {
		QualificationInstitution institution = new QualificationInstitution();
		
		expect(qualificationInstitutionDAO.getInstitutionByCode("aa")).andReturn(institution);
		
		replay();
		QualificationInstitution returned = service.getInstitutionByCode("aa");
		verify();
		
		assertSame(institution, returned);
	}

	@Test
	public void shouldCreateNewCustomInstitutionIfCustomInstitutionExists(){
		QualificationInstitution institution = new QualificationInstitution();
		QualificationInstitution lastCustomInstitution = new QualificationInstitutionBuilder().code("CUST00084").build();
		
		
		expect(qualificationInstitutionDAO.getLastCustomInstitution()).andReturn(lastCustomInstitution);
		qualificationInstitutionDAO.save(institution);
		
		replay();
		service.createNewCustomInstitution(institution);
		verify();
		
		assertEquals("CUST00085", institution.getCode());
	}
	
	@Test
	public void shouldCreateNewCustomInstitutionIfNoCustomInstitutionExist(){
		QualificationInstitution institution = new QualificationInstitution();
		
		
		expect(qualificationInstitutionDAO.getLastCustomInstitution()).andReturn(null);
		qualificationInstitutionDAO.save(institution);
		
		replay();
		service.createNewCustomInstitution(institution);
		verify();
		
		assertEquals("CUST00000", institution.getCode());
	}
	
}
