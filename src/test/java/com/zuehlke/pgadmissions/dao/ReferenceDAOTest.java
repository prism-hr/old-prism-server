package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReferenceCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.UserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ReferenceDAOTest extends AutomaticRollbackTestCase {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        ReferenceDAO referenceDAO = new ReferenceDAO();
        referenceDAO.getReferenceById(1);
    }

    @Test
    public void shouldGetReferenceById() {
        User user = new UserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .enabled(false).build();

        save(user);

        ApplicationForm application = new ApplicationFormBuilder().advert(testObjectProvider.getEnabledProgram()).applicant(user)
                .status(new State().withId(ApplicationFormStatus.VALIDATION)).build();
        save(application);

        ReferenceComment reference = new ReferenceCommentBuilder().user(user).comment("comment").application(application).build();
        sessionFactory.getCurrentSession().save(reference);
        flushAndClearSession();

        ReferenceDAO referenceDAO = new ReferenceDAO(sessionFactory);
        ReferenceComment returnedReference = referenceDAO.getReferenceById(reference.getId());
        assertEquals(reference.getId(), returnedReference.getId());
    }

}
