package com.zuehlke.pgadmissions.rest.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.domain.user.UserEmploymentPosition;
import com.zuehlke.pgadmissions.domain.user.UserReferee;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mapping.UserMapper;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAdditionalInformationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileAddressDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileDocumentDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileEmploymentPositionDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfilePersonalDetailDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileQualificationDTO;
import com.zuehlke.pgadmissions.rest.dto.profile.ProfileRefereeDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceListFilterDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserActivateDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserEmailDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserSimpleDTO;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserProfileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.rest.validation.UserLinkingValidator;
import com.zuehlke.pgadmissions.rest.validation.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProfileService;
import com.zuehlke.pgadmissions.services.ResourceListFilterService;
import com.zuehlke.pgadmissions.services.UserAccountService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    private EntityService entityService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ProfileService profileService;

    @Inject
    private ResourceListFilterService resourceListFilterService;

    @Inject
    private UserService userService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private UserLinkingValidator userLinkingValidator;

    @Inject
    private UserRegistrationValidator userRegistrationValidator;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public UserRepresentationExtended getUser() {
        return userMapper.getUserRepresentationExtended(userService.getCurrentUser());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.PUT)
    public void updateUser(@RequestBody UserSimpleDTO userDTO) {
        userService.updateUser(userDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/linkedUsers", method = RequestMethod.POST)
    public UserRepresentationSimple linkUsers(@RequestBody @Valid UserLinkingDTO userLinkingDTO) {
        User parentUser = userService.getCurrentUser().getParentUser();
        User otherUser = userService.getUserByEmail(userLinkingDTO.getOtherEmail());
        userService.linkUsers(parentUser, otherUser);
        return userMapper.getUserRepresentationSimple(otherUser);
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
        UserRepresentationSimple userRepresentation = userMapper.getUserRepresentationSimple(user);

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
    public List<UserRepresentationSimple> getSimilarUsers(@RequestParam String searchTerm) {
        return userService.getSimilarUsers(searchTerm);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/activity", method = RequestMethod.GET, params = "permissionScope")
    public UserActivityRepresentation getActivitySummary(@RequestParam PrismScope permissionScope) {
        return userMapper.getUserActivityRepresentation(userService.getCurrentUser(), permissionScope);
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
    public ResourceListFilterDTO getFilter(@PathVariable String resourceScope) {
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

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@PathVariable Integer userId, @Valid @RequestBody ProfilePersonalDetailDTO personalDetailDTO) throws Exception {
        profileService.updatePersonalDetailUser(userId, personalDetailDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/address", method = RequestMethod.PUT)
    public void saveAddress(@PathVariable Integer userId, @Valid @RequestBody ProfileAddressDTO addressDTO) throws Exception {
        profileService.updateAddressUser(userId, addressDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer userId, @PathVariable Integer qualificationId,
            @Valid @RequestBody ProfileQualificationDTO qualificationDTO) throws Exception {
        profileService.updateQualificationUser(userId, qualificationId, qualificationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer userId, @PathVariable Integer qualificationId) throws Exception {
        profileService.deleteQualificationUser(userId, qualificationId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@PathVariable Integer userId,
            @Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) throws Exception {
        UserEmploymentPosition employmentPosition = profileService.updateEmploymentPositionUser(userId, null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer userId, @PathVariable Integer employmentPositionId,
            @Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) throws Exception {
        profileService.updateEmploymentPositionUser(userId, employmentPositionId, employmentPositionDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@PathVariable Integer userId, @Valid @RequestBody ProfileRefereeDTO refereeDTO) throws Exception {
        UserReferee referee = profileService.updateRefereeUser(userId, null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer userId, @PathVariable Integer refereeId, @Valid @RequestBody ProfileRefereeDTO refereeDTO) throws Exception {
        profileService.updateRefereeUser(userId, refereeId, refereeDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer userId, @PathVariable Integer employmentPositionId) throws Exception {
        profileService.deleteEmploymentPositionUser(userId, employmentPositionId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer userId, @PathVariable Integer refereeId) throws Exception {
        profileService.deleteRefereeUser(userId, refereeId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/document", method = RequestMethod.PUT)
    public void saveDocument(@PathVariable Integer userId, @Valid @RequestBody ProfileDocumentDTO documentDTO) throws Exception {
        profileService.updateDocumentUser(userId, documentDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@PathVariable Integer userId, @Valid @RequestBody ProfileAdditionalInformationDTO additionalInformationDTO) throws Exception {
        profileService.updateAdditionalInformationUser(userId, additionalInformationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/profile", method = RequestMethod.GET)
    public UserProfileRepresentation getUserProfile(@PathVariable Integer userId) {
        return userMapper.getUserProfileRepresentation(userId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{userId}/profile/share", method = RequestMethod.PUT)
    public void shareUserProfile(@PathVariable Integer userId, @RequestParam(required = true) Boolean shareProfile) {
        userAccountService.shareUserProfile(userId, shareProfile);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "profiles", method = RequestMethod.GET)
    public List<ProfileListRowRepresentation> getUserProfiles(@RequestBody ProfileListFilterDTO filter) {
        return userMapper.getProfileListRowRepresentations(filter);
    }

}
