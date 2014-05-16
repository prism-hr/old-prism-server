package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder.aOpportunityRequest;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Matcher;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;

public class OpportunityRequestDAOTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldFindOpportunityRequestById() {
        User user = (User) sessionFactory.getCurrentSession().get(User.class, 15);
        InstitutionDomicile domicile = (InstitutionDomicile) sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).build();

        save(request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        OpportunityRequest returned = opportunityRequestDAO.findById(request2.getId());

        assertEquals(request2.getId(), returned.getId());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void shouldGetInitialOpportunityRequests() {
        User currentUser = (User) sessionFactory.getCurrentSession().get(User.class, 28);
        User otherUser = (User) sessionFactory.getCurrentSession().get(User.class, 15);
        InstitutionDomicile domicile = (InstitutionDomicile) sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();
        Program program = testObjectProvider.getEnabledProgram();

        DateTime date = new DateTime(1410, 7, 14, 12, 0);

        OpportunityRequest request1 = aOpportunityRequest(currentUser, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(currentUser, domicile).sourceProgram(program).createdDate(date.toDate()).build();
        OpportunityRequest request3 = aOpportunityRequest(currentUser, domicile).sourceProgram(program).createdDate(date.plusSeconds(8).toDate()).build();
        OpportunityRequest request4 = aOpportunityRequest(otherUser, domicile).createdDate(date.plusSeconds(8).toDate()).build();

        save(program, request1, request2, request3, request4);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        List<OpportunityRequest> returned = opportunityRequestDAO.listOpportunityRequests(currentUser);

        assertThat(returned, (Matcher) hasItem(hasProperty("id", equalTo(request1.getId()))));
        assertThat(returned, (Matcher) hasItem(hasProperty("id", equalTo(request3.getId()))));
        assertThat(returned, (Matcher) not(hasItem(hasProperty("id", equalTo(request2.getId())))));
        assertThat(returned, (Matcher) not(hasItem(hasProperty("id", equalTo(request4.getId())))));
    }

    @Test
    public void shouldGetOpportunityRequestsForProgram() {
        User user = (User) sessionFactory.getCurrentSession().get(User.class, 15);
        InstitutionDomicile domicile = (InstitutionDomicile) sessionFactory.getCurrentSession().createCriteria(InstitutionDomicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();
        Program program = testObjectProvider.getEnabledProgram();

        DateTime date = new DateTime(1410, 7, 14, 12, 0);

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.toDate()).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.plusSeconds(8).toDate()).build();

        save(program, request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        List<OpportunityRequest> returned = opportunityRequestDAO.getOpportunityRequests(program);

        assertThat(returned, contains(request3, request2));
    }

}
