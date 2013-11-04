package com.zuehlke.pgadmissions.dao;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.dao.mappings.AutomaticRollbackTestCase;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRole;
import com.zuehlke.pgadmissions.domain.ApplicationFormUserRoleId;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Role;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormUserRoleBuilder;
import com.zuehlke.pgadmissions.domain.builders.ProgramBuilder;
import com.zuehlke.pgadmissions.domain.builders.RegisteredUserBuilder;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;

public class ApplicationFormUserRoleDAOTest extends AutomaticRollbackTestCase {

    private RoleDAO roleDAO;
    private ApplicationFormUserRoleDAO applicationFormUserRoleDAO;

    private RegisteredUser user;
    private Program program;
    private ApplicationForm application;

    @Test
    public void shouldSaveAndOverrideApplicationFormUserRole() throws ParseException {

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);

        ApplicationFormUserRoleId id = new ApplicationFormUserRoleId(application, user, refereeRole);

        ApplicationFormUserRole applicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .currentRole(true).build();
        applicationFormUserRoleDAO.save(applicationFormUserRole);

        flushAndClearSession();

        ApplicationFormUserRole updatedApplicationFormUserRole = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole)
                .currentRole(false).build();
        applicationFormUserRoleDAO.save(updatedApplicationFormUserRole);

        flushAndClearSession();

        ApplicationFormUserRole returnedRole = applicationFormUserRoleDAO.findById(id);
        assertFalse(returnedRole.isCurrentRole());
    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationFormAndAuthority() throws ParseException {

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole).currentRole(true)
                .build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole).currentRole(true)
                .build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .currentRole(true).build();
        save(role1, role2, role3);

        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationFormAndAuthorities(application, Authority.REFEREE,
                Authority.SUPERADMINISTRATOR);
        assertThat(roles, contains(role1, role3));
    }

    @Test
    public void shouldFindApplicationFormUserRoleByApplicationForm() throws ParseException {

        Role refereeRole = roleDAO.getRoleByAuthority(Authority.REFEREE);
        Role admitterRole = roleDAO.getRoleByAuthority(Authority.ADMITTER);
        Role superAdministratorRole = roleDAO.getRoleByAuthority(Authority.SUPERADMINISTRATOR);

        ApplicationFormUserRole role1 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(refereeRole).currentRole(true)
                .build();
        ApplicationFormUserRole role2 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(admitterRole).currentRole(true)
                .build();
        ApplicationFormUserRole role3 = new ApplicationFormUserRoleBuilder().applicationForm(application).user(user).role(superAdministratorRole)
                .currentRole(true).build();
        save(role1, role2, role3);

        List<ApplicationFormUserRole> roles = applicationFormUserRoleDAO.findByApplicationForm(application);
        assertThat(roles, containsInAnyOrder(role1, role2, role3));
    }

    @Before
    public void prepare() throws ParseException {
        roleDAO = new RoleDAO(sessionFactory);
        applicationFormUserRoleDAO = new ApplicationFormUserRoleDAO(sessionFactory);

        user = new RegisteredUserBuilder().firstName("Jane").lastName("Doe").email("email@test.com").username("username").password("password")
                .accountNonExpired(false).accountNonLocked(false).credentialsNonExpired(false).enabled(false).build();

        program = new ProgramBuilder().code("doesntexist").title("another title").build();

        Date lastUpdatedDate = new SimpleDateFormat("dd MM yyyy hh:mm:ss").parse("01 06 2011 14:05:23");
        application = new ApplicationForm();
        application.setApplicant(user);
        application.setLastUpdated(lastUpdatedDate);
        application.setProgram(program);
        application.setStatus(ApplicationFormStatus.APPROVED);
        application.setApplicationNumber("ABC");

        save(user, program, application);

        flushAndClearSession();
    }

}
