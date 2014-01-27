package com.zuehlke.pgadmissions.services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.UnitilsJUnit4TestClassRunner;
import org.unitils.easymock.annotation.Mock;
import org.unitils.inject.annotation.InjectIntoByType;
import org.unitils.inject.annotation.TestedObject;

import com.zuehlke.pgadmissions.dao.ApplicationFormUserRoleDAO;
import com.zuehlke.pgadmissions.dao.RoleDAO;
import com.zuehlke.pgadmissions.dao.UserDAO;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;

@RunWith(UnitilsJUnit4TestClassRunner.class)
public class ApplicationFormUserRoleServiceTest {

    @Mock
    @InjectIntoByType
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAOMock;

    @Mock
    @InjectIntoByType
    private RoleDAO roleDAOMock;

    @Mock
    @InjectIntoByType
    private UserDAO userDAOMock;

    @TestedObject
    private ApplicationFormUserRoleService service;

    private ApplicationForm application;

    private RegisteredUser applicant;
    
    @Test
    @Ignore
    public void shouldAssignRolesWhenApplicationSubmitted(){
        // TODO implement tests
        service.applicationSubmitted(application);
    }

    @Before
    public void prepare() {
        applicant = new RegisteredUser();
        application = new ApplicationFormBuilder().applicant(applicant).build();
    }

}
