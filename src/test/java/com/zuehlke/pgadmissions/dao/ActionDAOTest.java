package com.zuehlke.pgadmissions.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.Action;
import com.zuehlke.pgadmissions.domain.enums.SystemAction;

public class ActionDAOTest extends AutomaticRollbackTestCase {

    private ActionDAO actionDAO;

    @Test
    public void shouldGetActionById() {
        Action returnedAction = actionDAO.getById(SystemAction.APPLICATION_VIEW_AS_REFEREE);
        assertNotNull(returnedAction);
    }

    @Before
    public void prepare() {
        actionDAO = new ActionDAO(sessionFactory);
    }

}