package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class OpportunityRequestDAOTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldFindOpportunityRequestById() {
		RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
		Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();

		OpportunityRequest request1 = new OpportunityRequestBuilder().author(user).createdDate(new Date()).institutionCode("aaa").institutionCountry(domicile)
				.programDescription("desc").programTitle("title").build();
		OpportunityRequest request2 = new OpportunityRequestBuilder().author(user).createdDate(new Date()).institutionCode("bbb").institutionCountry(domicile)
				.programDescription("desc").programTitle("title").build();
		OpportunityRequest request3 = new OpportunityRequestBuilder().author(user).createdDate(new Date()).institutionCode("ccc").institutionCountry(domicile)
				.programDescription("desc").programTitle("title").build();

		save(request1, request2, request3);

		OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
		OpportunityRequest returned  = opportunityRequestDAO.findById(request2.getId());
		
		assertEquals("bbb", returned.getInstitutionCode());
			
	}

}
