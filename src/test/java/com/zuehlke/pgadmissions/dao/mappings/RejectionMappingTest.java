package com.zuehlke.pgadmissions.dao.mappings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.RejectReasonDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.QualificationInstitution;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.RejectReason;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.QualificationInstitutionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;

public class RejectionMappingTest extends AutomaticRollbackTestCase {

    @Test
    public void shouldSaveAndLoadRejection() {
        RejectReasonDAO rejectReasonDAO = new RejectReasonDAO(sessionFactory);
        RejectReason rejectReason = rejectReasonDAO.getAllReasons().get(0);
        Rejection rejection = new RejectionBuilder().includeProspectusLink(true).rejectionReason(rejectReason).build();

        sessionFactory.getCurrentSession().save(rejection);
        assertNotNull(rejection.getId());

        Rejection reloadedRejection = (Rejection) sessionFactory.getCurrentSession().get(Rejection.class, rejection.getId());
        assertEquals(rejection, reloadedRejection);
        assertSame(rejection, reloadedRejection);

        flushAndClearSession();
        reloadedRejection = (Rejection) sessionFactory.getCurrentSession().get(Rejection.class, rejection.getId());
        assertEquals(rejection.getId(), reloadedRejection.getId());
        assertNotSame(rejection, reloadedRejection);
        assertEquals(rejectReason.getId(), reloadedRejection.getRejectionReason().getId());
        assertTrue(reloadedRejection.isIncludeProspectusLink());
    }

    @Test
    public void shouldLoadApformWithRejection() {
        RegisteredUser user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        QualificationInstitution institution = new QualificationInstitutionBuilder().code("code").name("a").countryCode("AE").enabled(true).build();
        Program program = new ProgramBuilder().code("doesntexist").title("another title").institution(institution).build();

        save(user, institution, program);
        ApplicationForm application = new ApplicationFormBuilder().program(program).applicant(user).build();
        save(application);

        RejectReasonDAO rejectReasonDAO = new RejectReasonDAO(sessionFactory);
        RejectReason rejectReason = rejectReasonDAO.getAllReasons().get(0);
        Rejection rejection = new RejectionBuilder().includeProspectusLink(true).rejectionReason(rejectReason).build();

        application.setRejection(rejection);
        save(application);

        flushAndClearSession();

        Rejection reloadedRejection = (Rejection) sessionFactory.getCurrentSession().get(Rejection.class, rejection.getId());
        assertEquals(application.getId(), reloadedRejection.getApplicationForm().getId());
    }
}
