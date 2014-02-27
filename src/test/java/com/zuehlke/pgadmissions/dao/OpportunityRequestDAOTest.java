package com.zuehlke.pgadmissions.dao;

import static com.zuehlke.pgadmissions.domain.builders.OpportunityRequestBuilder.aOpportunityRequest;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.OpportunityRequest;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;

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
    public void shouldGetInitialOpportunityRequests() {
        RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
        Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();
        QualificationInstitution institution = QualificationInstitutionBuilder.aQualificationInstitution().build();
        Program program = testObjectProvider.getEnabledProgram();

        DateTime date = new DateTime(1410, 7, 14, 12, 0);

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.toDate()).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.plusSeconds(8).toDate()).build();

        save(institution, program, request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        List<OpportunityRequest> returned = opportunityRequestDAO.getInitialOpportunityRequests();

        assertThat(returned, hasItems(request3));
        assertThat(returned, not(hasItems(request1, request2)));
    }

    @Test
    public void shouldGetOpportunityRequestsForProgram() {
        RegisteredUser user = (RegisteredUser) sessionFactory.getCurrentSession().get(RegisteredUser.class, 15);
        Domicile domicile = (Domicile) sessionFactory.getCurrentSession().createCriteria(Domicile.class).add(Restrictions.eq("code", "XK")).uniqueResult();
        QualificationInstitution institution = QualificationInstitutionBuilder.aQualificationInstitution().build();
        Program program = testObjectProvider.getEnabledProgram();

        DateTime date = new DateTime(1410, 7, 14, 12, 0);

        OpportunityRequest request1 = aOpportunityRequest(user, domicile).build();
        OpportunityRequest request2 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.toDate()).build();
        OpportunityRequest request3 = aOpportunityRequest(user, domicile).sourceProgram(program).createdDate(date.plusSeconds(8).toDate()).build();

        save(institution, program, request1, request2, request3);

        OpportunityRequestDAO opportunityRequestDAO = new OpportunityRequestDAO(sessionFactory);
        List<OpportunityRequest> returned = opportunityRequestDAO.getOpportunityRequests(program);

        assertThat(returned, contains(request3, request2));
    }

}
