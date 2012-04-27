package com.zuehlke.pgadmissions.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.AdditionalInformationBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class AdditionalInfoDAOTest extends AutomaticRollbackTestCase {

	private ApplicationForm applicationForm;

	@Before
	@Override
	public void setUp() {
		super.setUp();
		Program program = new ProgramBuilder().code("newproject").title("another title").toProgram();
		save(program);
		RegisteredUser applicant = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).toUser();
		save(applicant);

		applicationForm = new ApplicationFormBuilder().applicant(applicant).program(program).toApplicationForm();
		save(applicationForm);
		flushAndClearSession();
	}

	@Test
	public void storeFullAdditionalInfo() {
		AdditionalInfoDAO infoDAO = new AdditionalInfoDAO(sessionFactory);
		String infoText = "blablabal";
		String conText = "streaking in public";
		AdditionalInformation info = new AdditionalInformationBuilder()//
				.informationText(infoText)//
				.setConvictions(true).convictionsText(conText)//
				.applicationForm(applicationForm).toAdditionalInformation();

		infoDAO.save(info);
		Integer persistentID = info.getId();
		Assert.assertNotNull(persistentID);
		flushAndClearSession();

		AdditionalInformation storedInfo = (AdditionalInformation) sessionFactory.getCurrentSession()//
				.load(AdditionalInformation.class, persistentID);

		Assert.assertNotNull(storedInfo);
		Assert.assertEquals(persistentID, storedInfo.getId());
		Assert.assertTrue(storedInfo.hasConvictions());
		Assert.assertEquals(infoText, storedInfo.getInformationText());
		Assert.assertEquals(conText, storedInfo.getConvictionsText());
	}

	public void storeMinimumAdditionalInfo() {
		AdditionalInfoDAO infoDAO = new AdditionalInfoDAO(sessionFactory);
		AdditionalInformation info = new AdditionalInformationBuilder()//
				.setConvictions(false)//
				.applicationForm(applicationForm).toAdditionalInformation();

		infoDAO.save(info);
		Integer persistentID = info.getId();
		Assert.assertNotNull(persistentID);
		flushAndClearSession();

		AdditionalInformation storedInfo = (AdditionalInformation) sessionFactory.getCurrentSession()//
				.load(AdditionalInformation.class, persistentID);

		Assert.assertNotNull(storedInfo);
		Assert.assertEquals(persistentID, storedInfo.getId());
		Assert.assertFalse(storedInfo.hasConvictions());
	}
}
