package com.zuehlke.pgadmissions.integration.helpers;

import static org.junit.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType;
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

    public void verifyResourceOwnerReassignment() throws Exception {
        Program program = programService.getProgramByImportedCode(null, "RRDSCSSING01");
        User program2NewAdmin = userService.getOrCreateUserWithRoles("Alex", "Salmond", "alex@salmond.com", program,
                Sets.newHashSet(PrismRole.PROGRAM_VIEWER, PrismRole.PROGRAM_ADMINISTRATOR));
        roleService.updateUserRole(program, program.getUser(), PrismRole.PROGRAM_ADMINISTRATOR, PrismRoleTransitionType.DELETE);
        assertEquals(program2NewAdmin, program.getUser());
    }

}
