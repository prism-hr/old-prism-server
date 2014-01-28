package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder.aOpportunityRequest;
import static org.junit.Assert.assertEquals;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;

public class OpportunityRequestDAOTest extends AutomaticRollbackTestCase {

	@Test
	public void shouldFindOpportunityRequestById() {
		RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
		Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();

		OpportunityRequest request1 = aOpportunityRequest(user, domicile).institutionCode("aaa").build(); 
		OpportunityRequest request2 = aOpportunityRequest(user, domicile).institutionCode("bbb").build(); 
		OpportunityRequest request3 = aOpportunityRequest(user, domicile).institutionCode("ccc").build(); 
				
		save(request1, request2, request3);

		OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
		OpportunityRequest returned  = opportunityRequestDAO.findById(request2.getId());
		
		assertEquals("bbb", returned.getInstitutionCode());
			
	}

}
