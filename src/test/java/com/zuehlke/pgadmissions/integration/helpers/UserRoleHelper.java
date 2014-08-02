package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.rest.representation.AbstractResourceRepresentation;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@Service
@Transactional
public class UserRoleHelper {

    @Autowired
    private ProgramService programService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public void verifyResourceOwnerReassignment() {
        Program program = programService.getProgramByImportedCode(null, "RRDSCSSING01");
        User program2NewAdmin = userService.getOrCreateUserWithRoles("Alex", "Salmond", "alex@salmond.com", program,
                Lists.newArrayList(new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_ADMINISTRATOR, true)));
        roleService.updateRoles(program, program.getUser(),
                Lists.newArrayList(new AbstractResourceRepresentation.RoleRepresentation(PrismRole.PROGRAM_ADMINISTRATOR, false)));
        assertEquals(program2NewAdmin, program.getUser());
    }

}