package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.EmploymentPositionBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;

public class EmploymentPositionDAOTest extends AutomaticRollbackTestCase {

    private RegisteredUser user;

    private Program program;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        EmploymentPositionDAO positionDAO = new EmploymentPositionDAO();
        EmploymentPosition position = new EmploymentPositionBuilder().id(1).toEmploymentPosition();
        positionDAO.delete(position);
    }

    @Test
    public void shouldDeleteEmploymentPosition() {
        ApplicationForm application = new ApplicationForm();
        application.setAdvert(program);
        application.setApplicant(user);

        EmploymentPosition employmentPosition = new EmploymentPositionBuilder().address1("Address").application(application).employerName("fr")
                .endDate(new Date()).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();
        save(application, employmentPosition);
        flushAndClearSession();

        Integer id = employmentPosition.getId();
        EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
        dao.delete(employmentPosition);
        flushAndClearSession();
        assertNull(sessionFactory.getCurrentSession().get(EmploymentPosition.class, id));
    }

    @Test
    public void shouldSaveEmployemnt() {
        ApplicationForm application = new ApplicationForm();
        application.setAdvert(program);
        application.setApplicant(user);

        save(application);
        flushAndClearSession();

        EmploymentPosition employmentPosition = new EmploymentPositionBuilder().address1("Address").application(application).employerName("fr")
                .endDate(new Date()).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();

        EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
        dao.save(employmentPosition);
        flushAndClearSession();

        assertEquals(employmentPosition.getId(),
                ((EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, employmentPosition.getId())).getId());
    }

    @Test
    public void shouldGetEmploymentById() {
        ApplicationForm application = new ApplicationForm();
        application.setAdvert(program);
        application.setApplicant(user);

        EmploymentPosition employmentPosition = new EmploymentPositionBuilder().address1("Address").application(application).employerName("fr")
                .endDate(new Date()).remit("dfsfsd").startDate(new Date()).position("rerew").toEmploymentPosition();
        save(application, employmentPosition);
        flushAndClearSession();

        EmploymentPositionDAO dao = new EmploymentPositionDAO(sessionFactory);
        EmploymentPosition reloadedPosistion = dao.getById(employmentPosition.getId());
        assertEquals(employmentPosition.getId(), reloadedPosistion.getId());

    }

    @Before
    public void prepare() {
        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password").enabled(false)
                .build();
        save(user);
        flushAndClearSession();
        program = testObjectProvider.getEnabledProgram();
    }
}
