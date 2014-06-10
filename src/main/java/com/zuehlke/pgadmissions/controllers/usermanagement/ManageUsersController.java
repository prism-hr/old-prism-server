package com.zuehlke.pgadmissions.controllers.usermanagement;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.ProgramPropertyEditor;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.ManageUsersService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.SuperadminUserDTOValidator;
import com.zuehlke.pgadmissions.validators.UserDTOValidator;

@Controller
@RequestMapping("/manageUsers")
public class ManageUsersController {

    private static final String NEW_USER_VIEW_NAME = "private/staff/superAdmin/create_new_user_in_role_page";

    @Autowired
    private ProgramService programService;
    
    @Autowired
    private SystemService systemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ManageUsersService manageUsersService;

    @Autowired
    private ProgramPropertyEditor programPropertyEditor;

    @Autowired
    private UserDTOValidator newUserDTOValidator;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SuperadminUserDTOValidator userDTOValidator;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private RoleService roleService;

    @InitBinder(value = "userDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(newUserDTOValidator);
        binder.registerCustomEditor(Program.class, programPropertyEditor);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @InitBinder(value = "adminDTO")
    public void registerValidator(WebDataBinder binder) {
        binder.setValidator(userDTOValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @ModelAttribute("programs")
    public List<Program> getPrograms() {
        User currentUser = userService.getCurrentUser();
        if (roleService.hasRole(currentUser, PrismRole.SYSTEM_ADMINISTRATOR)) {
            return programService.getAllEnabledPrograms();
        }
        return roleService.getProgramsByUserAndRole(currentUser, PrismRole.PROGRAM_ADMINISTRATOR);
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("superadministrators")
    public List<User> getSuperadministrators() {
        List<User> superadmins = roleService.getUsersInRole(systemService.getSystem(), PrismRole.SYSTEM_ADMINISTRATOR);
        return superadmins;
    }

    @ModelAttribute("admitters")
    public List<User> getAdmitters() {
        return roleService.getUsersInRole(systemService.getSystem(), PrismRole.INSTITUTION_ADMITTER);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/edit")
    public String getAddUsersView() {
        User user = userService.getCurrentUser();
        if (!roleService.hasAnyRole(user, PrismRole.SYSTEM_ADMINISTRATOR, PrismRole.PROGRAM_ADMINISTRATOR, PrismRole.INSTITUTION_ADMITTER)) {
            throw new ResourceNotFoundException();
        }
        return NEW_USER_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveSuperadmin")
    public String handleAddSuperadmin(@Valid @ModelAttribute("adminDTO") UserDTO userDTO, BindingResult result) {
        User user = userService.getCurrentUser();
        if (Arrays.asList(userDTO.getSelectedAuthorities()).contains(PrismRole.SYSTEM_ADMINISTRATOR) && !roleService.hasRole(user, PrismRole.SYSTEM_ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }

        if (roleService.hasRole(user, PrismRole.SYSTEM_ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }
        
        boolean userExisted = userService.getUserByEmail(userDTO.getEmail()) != null;

        User newUser = manageUsersService.setUserRoles(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), false, systemService.getSystem(),
                userDTO.getSelectedAuthorities());
        
        if(!userExisted) {
            
        }

        return "redirect:/manageUsers/edit";

    }

    @RequestMapping(method = RequestMethod.POST, value = "/edit/saveUser")
    public String handleEditUserRoles(@Valid @ModelAttribute("userDTO") UserDTO userDTO, BindingResult result) {
        User user = userService.getCurrentUser();
        if (!roleService.hasAnyRole(user, PrismRole.SYSTEM_ADMINISTRATOR, PrismRole.PROGRAM_ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }

        if (result.hasErrors()) {
            return NEW_USER_VIEW_NAME;
        }

        manageUsersService.setUserRoles(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(), true, userDTO.getSelectedProgram(),
                userDTO.getSelectedAuthorities());

        return "redirect:/manageUsers/edit?programCode=" + userDTO.getSelectedProgram().getCode();
    }

}
