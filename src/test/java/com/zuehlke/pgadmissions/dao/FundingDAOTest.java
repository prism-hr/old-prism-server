package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.FundingBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.FundingType;

public class FundingDAOTest extends AutomaticRollbackTestCase {

	private RegisteredUser user;
	private Program program;

	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerException() {
		FundingDAO fundingDAO = new FundingDAO();
		Funding funding = new FundingBuilder().id(1).build();
		fundingDAO.delete(funding);
	}

	@Test
	public void shouldDeleteFunding() {
		ApplicationForm application = new ApplicationForm();
		application.setAdvert(program);
		application.setApplicant(user);
		
		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432")
				.build();
		save(application, funding);
		flushAndClearSession();

		Integer id = funding.getId();
		FundingDAO fundingDAO = new FundingDAO(sessionFactory);
		fundingDAO.delete(funding);
		flushAndClearSession();
		assertNull(sessionFactory.getCurrentSession().get(Funding.class, id));
	}

	@Test
	public void shouldGetFundingById() {
		ApplicationForm application = new ApplicationForm();
		application.setAdvert(program);
		application.setApplicant(user);
		
		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432")
				.build();
		save(application, funding);

		Integer id = funding.getId();
		FundingDAO fundingDAO = new FundingDAO(sessionFactory);
		Funding reloadedFunding = fundingDAO.getFundingById(id);
		assertEquals(funding, reloadedFunding);
	}

	@Test
	public void shouldSaveFunding() {
		ApplicationForm application = new ApplicationForm();
		application.setAdvert(program);
		application.setApplicant(user);
		
		save(application);
		flushAndClearSession();
		Funding funding = new FundingBuilder().application(application).awardDate(new Date()).description("fi").type(FundingType.EMPLOYER).value("34432")
				.build();
		FundingDAO fundingDAO = new FundingDAO(sessionFactory);
		fundingDAO.save(funding);

		flushAndClearSession();
		Funding returnedFunding = (Funding) sessionFactory.getCurrentSession().get(Funding.class, funding.getId());
		assertEquals(funding.getId(), returnedFunding.getId());
	}

	@Before
	public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com")
                .username("username").password("password").accountNonExpired(false).accountNonLocked(false)
                .credentialsNonExpired(false).enabled(false).build();
        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
	}
}
