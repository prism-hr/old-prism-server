package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class AdditionalInfoDAOTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;

	@Before
	public void prepare() {
	    QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a54").domicileCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("newproject").title("another title").institution(institution).build();
        save(institution, program);
        RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe")
                .email("email@test.com").username("username").password("password").accountNonExpired(false)
                .accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();
        save(applicant);
		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).build();
		save(applicationForm);
		flushAndClearSession();
	}

	@Test(expected = NullPointerException.class)
	public void testSetupFailure() {
		AdditionalInfoDAO infoDAO = new AdditionalInfoDAO();
		AdditionalInformation info = new AdditionalInformationBuilder()//
				.setConvictions(false)//
				.applicationForm(applicationForm).build();
		infoDAO.save(info);
	}

	@Test
	public void storeFullAdditionalInfo() {
		AdditionalInfoDAO infoDAO = new AdditionalInfoDAO(sessionFactory);
		String infoText = "blablabal";
		String conText = "streaking in public";
		AdditionalInformation info = new AdditionalInformationBuilder()//
				.setConvictions(true).convictionsText(conText)//
				.applicationForm(applicationForm).build();

		infoDAO.save(info);
		Integer persistentID = info.getId();
		Assert.assertNotNull(persistentID);
		flushAndClearSession();

		AdditionalInformation storedInfo = (AdditionalInformation) sessionFactory.getCurrentSession()//
				.load(AdditionalInformation.class, persistentID);

		Assert.assertNotNull(storedInfo);
		Assert.assertEquals(persistentID, storedInfo.getId());
		Assert.assertTrue(storedInfo.getConvictions());
		Assert.assertEquals(conText, storedInfo.getConvictionsText());
	}

	@Test
	public void storeMinimumAdditionalInfo() {
		AdditionalInfoDAO infoDAO = new AdditionalInfoDAO(sessionFactory);
		AdditionalInformation info = new AdditionalInformationBuilder()//
				.setConvictions(false)//
				.applicationForm(applicationForm).build();

		infoDAO.save(info);
		Integer persistentID = info.getId();
		Assert.assertNotNull(persistentID);
		flushAndClearSession();

		AdditionalInformation storedInfo = (AdditionalInformation) sessionFactory.getCurrentSession()//
				.load(AdditionalInformation.class, persistentID);

		Assert.assertNotNull(storedInfo);
		Assert.assertEquals(persistentID, storedInfo.getId());
		Assert.assertFalse(storedInfo.getConvictions());
	}
}
