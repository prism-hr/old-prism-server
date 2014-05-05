package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.builders.TestData;
import com.zuehlke.pgadmissions.domain.enums.PrismState;

public class EmploymentPositionDAOTest extends AutomaticRollbackTestCase {

    private EmploymentPositionDAO employmentPositionDAO;
    
    private ApplicationForm application;
    
    

    @Test
    public void shouldDeleteEmploymentPosition() {
        EmploymentPosition employmentPosition = new EmploymentPosition().withApplication(application).withEmployerAddress(TestData.anAddress(testObjectProvider.getDomicile()))
                .withEmployerName("fr").withEndDate(new Date()).withRemit("dfsfsd").withStartDate(new Date()).withPosition("rerew");
        save(employmentPosition);
        flushAndClearSession();

        Integer id = employmentPosition.getId();
        employmentPositionDAO.delete(employmentPosition);
        flushAndClearSession();
        
        assertNull(sessionFactory.getCurrentSession().get(EmploymentPosition.class, id));
    }

    @Test
    public void shouldSaveEmployemnt() {
        EmploymentPosition employmentPosition = new EmploymentPosition().withApplication(application).withEmployerAddress(TestData.anAddress(testObjectProvider.getDomicile()))
                .withEmployerName("fr").withEndDate(new Date()).withRemit("dfsfsd").withStartDate(new Date()).withPosition("rerew");

        employmentPositionDAO.save(employmentPosition);
        flushAndClearSession();

        assertEquals(employmentPosition.getId(),
                ((EmploymentPosition) sessionFactory.getCurrentSession().get(EmploymentPosition.class, employmentPosition.getId())).getId());
    }

    @Test
    public void shouldGetEmploymentById() {
        EmploymentPosition employmentPosition = new EmploymentPosition().withApplication(application).withEmployerAddress(TestData.anAddress(testObjectProvider.getDomicile()))
                .withEmployerName("fr").withEndDate(new Date()).withRemit("dfsfsd").withStartDate(new Date()).withPosition("rerew");
        
        save(employmentPosition);
        flushAndClearSession();

        EmploymentPosition reloadedPosistion = employmentPositionDAO.getById(employmentPosition.getId());
        assertEquals(employmentPosition.getId(), reloadedPosistion.getId());

    }

    @Override
    public void setup() {
        super.setup();
        
        employmentPositionDAO = new EmploymentPositionDAO(sessionFactory);
        
        application = testObjectProvider.getApplication(PrismState.APPLICATION_UNSUBMITTED);
    }
}
