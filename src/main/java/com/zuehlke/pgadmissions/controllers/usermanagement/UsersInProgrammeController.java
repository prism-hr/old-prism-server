package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@Controller
@RequestMapping("/manageUsers")
public class UsersInProgrammeController {

    private static final String USERS_ROLES_VIEW = "private/staff/superAdmin/users_roles";

    @Autowired
    private UserService userService;

    @Autowired
    private ProgramService programsService;

    @Autowired
    private RoleService roleService;

    @ModelAttribute("usersInRoles")
    public List<RegisteredUser> getUsersInProgram(@RequestParam(required = false) String programCode) {
        // TODO implement
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/program")
    public String getUsersInProgramView() {
        RegisteredUser user = userService.getCurrentUser();
        if (!roleService.hasAnyRole(user, Authority.SUPERADMINISTRATOR, Authority.ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        return USERS_ROLES_VIEW;
    }

    @ModelAttribute("selectedProgram")
    public Program getSelectedProgram(@RequestParam(required = false) String programCode) {
        if (programCode == null) {
            return null;
        }
        return programsService.getProgramByCode(programCode);
    }

}
