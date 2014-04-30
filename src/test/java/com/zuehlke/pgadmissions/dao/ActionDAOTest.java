package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.builders.ActionBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.NotificationMethod;

public class ActionDAOTest extends AutomaticRollbackTestCase {
    
    private ActionDAO actionDAO;
    
    @Test
    public void shouldGetActionById() {
        Action action = new ActionBuilder().id(ApplicationFormAction.APPLICATION_PROVIDE_REFERENCE).notification(NotificationMethod.INDIVIDUAL).build();
        sessionFactory.getCurrentSession().update(action);
        Action returnedAction = actionDAO.getById(ApplicationFormAction.APPLICATION_PROVIDE_REFERENCE);
        assertSame(action, returnedAction);
    }
    
    @Before
    public void prepare() {
        actionDAO = new ActionDAO(sessionFactory);
    }


}