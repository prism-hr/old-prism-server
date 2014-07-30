package com.zuehlke.pgadmissions.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testWorkflowContext.xml")
@Service
public class IT3ManageUserRoles {

    @Autowired
    private IT2SystemReferenceDataImport it2SystemReferenceDataImport;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testManageUserRoles() throws Exception {
        it2SystemReferenceDataImport.testImportData();

        Program program = programService.getProgramByImportedCode(null, "RRDCOMSING01");

        User user = userService.getOrCreateUserWithRoles("Jozef", "Oleksy", "jozef@oleksy.pl", program, Lists.newArrayList(new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_VIEWER, true), new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_APPROVER, true)));

        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_VIEWER));
        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_APPROVER));
        assertFalse(roleService.hasUserRole(program, user, PrismRole.PROGRAM_ADMINISTRATOR));

        roleService.updateRoles(program, user, Lists.newArrayList(new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_VIEWER, true), new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_APPROVER, false), new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_ADMINISTRATOR, true)));

        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_VIEWER));
        assertFalse(roleService.hasUserRole(program, user, PrismRole.PROGRAM_APPROVER));
        assertTrue(roleService.hasUserRole(program, user, PrismRole.PROGRAM_ADMINISTRATOR));
        
        // TODO test reassigning owner role
    }
}
