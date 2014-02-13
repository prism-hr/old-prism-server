package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder.aOpportunityRequest;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.OpportunityRequestStatus;

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

//    @Test
//    public void shouldGetNewOpportunityRequests() {
//        RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
//        Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();
//
//        OpportunityRequest request1 = aOpportunityRequest(user, domicile).status(OpportunityRequestStatus.NEW).build();
//        OpportunityRequest request2 = aOpportunityRequest(user, domicile).status(OpportunityRequestStatus.APPROVED).build();
//        OpportunityRequest request3 = aOpportunityRequest(user, domicile).status(OpportunityRequestStatus.NEW).build();
//        OpportunityRequest request4 = aOpportunityRequest(user, domicile).status(OpportunityRequestStatus.REJECTED).build();
//
//        save(request1, request2, request3, request4);
//
//        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
//        List<OpportunityRequest> returned = opportunityRequestDAO.getNewOpportunityRequests();
//
//        assertThat(returned, hasItems(request1, request3));
//        assertThat(returned, not(hasItems(request2, request4)));
//    }
//
//    @Test
//    public void shouldGetInitialOpportunityRequests() {
//        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
//        List<OpportunityRequest> returned = opportunityRequestDAO.getInitialOpportunityRequests();
//        
//        System.out.println(Iterables.transform(returned, new Function<OpportunityRequest, String>() {
//            public String apply(OpportunityRequest r) {
//                return r.getId().toString();
//            }
//        }));
//    }
    
}
