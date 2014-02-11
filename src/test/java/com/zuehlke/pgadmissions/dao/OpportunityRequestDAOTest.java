package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder.aOpportunityRequest;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestType;

public class OpportunityRequestDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldFindOpportunityRequestById() {
        RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
        Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).build();

        save(request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        OpportunityRequest returned = opportunityRequestDAO.findById(request2.getId());

        assertEquals(request2.getId(), returned.getId());
    }

    @Test
    public void shouldGetInitialRequests() {
        RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
        Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).type(OpportunityRequestType.INITIAL).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).type(OpportunityRequestType.CHANGE).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).type(OpportunityRequestType.INITIAL).build();

        save(request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        List<OpportunityRequest> returned = opportunityRequestDAO.getInitialOpportunityRequests();

        assertThat(returned, hasItems(request1, request3));
        assertThat(returned, not(hasItem(request2)));
    }

}
