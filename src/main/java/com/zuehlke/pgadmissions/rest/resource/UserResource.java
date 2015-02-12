package com.zuehlke.pgadmissions.rest.resource;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.validation.Valid;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserActivateDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserEmailDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.UserLinkingValidator;
import com.zuehlke.pgadmissions.rest.validation.validator.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ResourceListFilterService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Named("authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRegistrationValidator userRegistrationValidator;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ResourceListFilterService resourceListFilterService;

    @Autowired
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Autowired
    private UserLinkingValidator userLinkingValidator;

    @Autowired
    private Mapper dozerBeanMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public UserExtendedRepresentation getUser() {
        User user = userService.getCurrentUser();
        UserExtendedRepresentation userRepresentation = dozerBeanMapper.map(user, UserExtendedRepresentation.class);
        userRepresentation.setPermissionPrecedence(roleService.getPermissionPrecedence(user));
        userRepresentation.setLinkedUsers(userService.getLinkedUserAccounts(user));
        userRepresentation.setParentUser(user.getParentUser().getEmail());
        return userRepresentation;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.PUT)
    public void updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public Map<String, String> authenticate(@RequestParam String username, @RequestParam String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        return ImmutableMap.of("token", authenticationTokenHelper.createToken(userDetails));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/linkedUsers", method = RequestMethod.POST)
    public UserRepresentation linkUsers(@RequestBody @Valid UserLinkingDTO userLinkingDTO) {
        User parentUser = userService.getCurrentUser().getParentUser();
        User otherUser = userService.getUserByEmail(userLinkingDTO.getOtherEmail());
        userService.linkUsers(parentUser, otherUser);
        return dozerBeanMapper.map(otherUser, UserRepresentation.class);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/linkedUsers/{email:.+}", method = RequestMethod.DELETE)
    public void unlinkUsers(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getParentUser().getChildUsers().contains(user)) {
            throw new AccessDeniedException("Cannot unlink user");
        }
        userService.unlinkUser(user.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/linkedUsers/selectParentUser", method = RequestMethod.POST)
    public void selectParentUser(@RequestBody UserEmailDTO email) {
        userService.selectParentUser(email.getEmail());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/switch", method = RequestMethod.POST)
    public Map<String, String> switchUser(@RequestParam String username) {
        User currentUser = userService.getCurrentUser();
        List<String> linkedUsers = userService.getLinkedUserAccounts(currentUser);
        if (!linkedUsers.contains(username)) {
            throw new AccessDeniedException("Users are not linked");
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        return ImmutableMap.of("token", authenticationTokenHelper.createToken(userDetails));
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void submitRegistration(@RequestHeader(value = "referer", required = false) String referrer,
            @Valid @RequestBody UserRegistrationDTO userRegistrationDTO) throws Exception {
        userService.registerUser(userRegistrationDTO, referrer);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/activate", method = RequestMethod.PUT)
    public Map<String, Object> activateAccount(@RequestBody UserActivateDTO activateDTO) {
        User user = userService.getUserByActivationCode(activateDTO.getActivationCode());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        String status;
        if (user.getUserAccount() == null) {
            status = "NOT_REGISTERED";
        } else {
            userService.activateUser(user.getId(), activateDTO.getActionId(), activateDTO.getResourceId());
            status = "ACTIVATED";
        }
        UserRepresentation userRepresentation = dozerBeanMapper.map(user, UserRepresentation.class);
        return ImmutableMap.of("status", status, "user", userRepresentation);
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@RequestParam String email) {
        userService.resetPassword(email);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/suggestion", method = RequestMethod.GET, params = "searchTerm")
    public List<UserRepresentation> getSimilarUsers(@RequestParam String searchTerm) {
        return userService.getSimilarUsers(searchTerm);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/filter/{resourceScope}", method = RequestMethod.PUT)
    public void saveFilter(@PathVariable String resourceScope, @RequestBody ResourceListFilterDTO filter) {
        PrismScope scope = PrismScope.valueOf(resourceScope.toUpperCase().substring(0, resourceScope.length() - 1));
        User currentUser = userService.getCurrentUser();
        try {
            resourceListFilterService.save(currentUser, entityService.getById(Scope.class, scope), filter);
        } catch (Exception e) {
            LOGGER.info("Error saving filter for user " + currentUser.toString(), e);
            throw new ResourceNotFoundException("Error saving filter");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/filter/{resourceScope}", method = RequestMethod.GET)
    public ResourceListFilterDTO getFilter(@PathVariable String resourceScope) throws DeduplicationException {
        PrismScope scope = PrismScope.valueOf(resourceScope.toUpperCase().substring(0, resourceScope.length() - 1));
        return resourceListFilterService.getByUserAndScope(userService.getCurrentUser(), entityService.getById(Scope.class, scope));
    }

    @InitBinder(value = "userRegistrationDTO")
    public void configureUserRegistrationBinding(WebDataBinder binder) {
        binder.setValidator(userRegistrationValidator);
    }

    @InitBinder(value = "userLinkingDTO")
    public void configureUserLinkingBinding(WebDataBinder binder) {
        binder.setValidator(userLinkingValidator);
    }

}
