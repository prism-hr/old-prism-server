package com.zuehlke.pgadmissions.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
import com.zuehlke.pgadmissions.integration.helpers.UserRoleHelper;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
@Ignore
public class IT5AssignInstitutionUsers {

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private EntityService entityService;

    @Autowired
    private UserRoleHelper userRoleHelper;

    @Autowired
    private IT4ImportInstitutionReferenceData it4;

    @Test
    public void run() throws Exception {
        it4.run();

        Program program = programService.getProgramByImportedCode(null, "RRDCOMSING01");

        User user = userService.getOrCreateUserWithRoles("Jozef", "Oleksy", "jozef@oleksy.pl", program, Sets.newHashSet(PrismRole.PROGRAM_VIEWER, PrismRole.PROGRAM_APPROVER));

        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_VIEWER));
        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_APPROVER));
        assertFalse(roleService.hasUserRole(program, user, PrismRole.PROGRAM_ADMINISTRATOR));

        roleService.updateUserRole(program, user, PrismRole.PROGRAM_VIEWER, PrismRoleTransitionType.CREATE);
        roleService.updateUserRole(program, user, PrismRole.PROGRAM_APPROVER, PrismRoleTransitionType.DELETE);

        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_VIEWER));
        assertFalse(roleService.hasUserRole(program, user, PrismRole.PROGRAM_APPROVER));
        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_ADMINISTRATOR));

        userRoleHelper.verifyResourceOwnerReassignment();
    }
}
