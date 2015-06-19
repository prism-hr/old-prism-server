package com.zuehlke.pgadmissions.rest.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserActivateDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserEmailDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.rest.representation.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.validation.validator.UserLinkingValidator;
import com.zuehlke.pgadmissions.rest.validation.validator.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.*;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    private UserRegistrationValidator userRegistrationValidator;

    @Inject
    private RoleService roleService;

    @Inject
    private UserService userService;

    @Inject
    private EntityService entityService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Inject
    private UserLinkingValidator userLinkingValidator;

    @Inject
    private Mapper dozerBeanMapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public UserExtendedRepresentation getUser() {
        User user = userService.getCurrentUser();
        UserExtendedRepresentation userRepresentation = dozerBeanMapper.map(user, UserExtendedRepresentation.class);
        userRepresentation.setPermissionPrecedence(roleService.getPermissionPrecedence(user));
        List<String> linkedUserAccounts = userService.getLinkedUserAccounts(user);
        userRepresentation.setLinkedUsers(linkedUserAccounts);
        userRepresentation.setParentUser(user.getEmail());

        Set<UserAccountExternal> externalAccounts = user.getUserAccount().getExternalAccounts();
        List<String> oauthProviders = Lists.newArrayListWithCapacity(externalAccounts.size());
        for (UserAccountExternal externalAccount : externalAccounts) {
            oauthProviders.add(externalAccount.getAccountType().getName());
        }
        userRepresentation.setOauthProviders(oauthProviders);
        userRepresentation.setRequiredFeedbackRoleCategory(userFeedbackService.getRoleCategoryUserFeedbackRequiredFor(user));

        return userRepresentation;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.PUT)
    public void updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
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
    @RequestMapping(value = "/activate", method = RequestMethod.PUT)
    public Map<String, Object> activateAccount(@RequestBody UserActivateDTO activateDTO) {
        User user = userService.getUserByActivationCode(activateDTO.getActivationCode());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        String status;
        String loginProvider = null;
        if (user.getUserAccount() == null) {
            status = "NOT_REGISTERED";
        } else {
            userService.activateUser(user.getId(), activateDTO.getActionId(), activateDTO.getResourceId());
            status = "ACTIVATED";
            UserAccountExternal primaryExternalAccount = user.getUserAccount().getPrimaryExternalAccount();
            loginProvider = primaryExternalAccount != null ? primaryExternalAccount.getAccountType().getName() : null;
        }
        UserRepresentation userRepresentation = dozerBeanMapper.map(user, UserRepresentation.class);

        Map<String, Object> result = Maps.newHashMap();
        result.put("status", status);
        result.put("user", userRepresentation);
        if (loginProvider != null) {
            result.put("loginProvider", loginProvider);
        }
        return result;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public void resetPassword(@RequestBody Map<String, String> body) {
        userService.resetPassword(body.get("email"));
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
