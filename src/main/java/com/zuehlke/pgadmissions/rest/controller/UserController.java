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
import com.zuehlke.pgadmissions.domain.user.UserEmploymentPosition;
import com.zuehlke.pgadmissions.domain.user.UserQualification;
import com.zuehlke.pgadmissions.domain.user.UserReferee;
import com.zuehlke.pgadmissions.domain.workflow.Scope;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.mapping.AdvertMapper;
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
import com.zuehlke.pgadmissions.rest.dto.user.UserAccountDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserActivateDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserEmailDTO;
import com.zuehlke.pgadmissions.rest.dto.user.UserLinkingDTO;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.ProfileListRowRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRepresentationConnection;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserProfileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.rest.validation.UserLinkingValidator;
import com.zuehlke.pgadmissions.rest.validation.UserRegistrationValidator;
import com.zuehlke.pgadmissions.security.AuthenticationTokenHelper;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.ProfileService;
import com.zuehlke.pgadmissions.services.ResourceListFilterService;
import com.zuehlke.pgadmissions.services.UserAccountService;
import com.zuehlke.pgadmissions.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Inject
    private AuthenticationTokenHelper authenticationTokenHelper;

    @Resource(name = "prismUserDetailsService")
    private UserDetailsService userDetailsService;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    private AdvertService advertService;

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
    @RequestMapping(value = "/account", method = RequestMethod.PUT)
    public void updateUserAccount(@RequestBody UserAccountDTO userAccountDTO) {
        userService.updateUserAccount(userAccountDTO);
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
            loginProvider = user.getUserAccount().getLinkedinId() != null ? "linkedin" : null;
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
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    public UserActivityRepresentation getActivitySummary() {
        return userMapper.getUserActivityRepresentation(userService.getCurrentUser());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/connection", method = RequestMethod.GET)
    public List<AdvertTargetRepresentation> getConnectionRepresentations() {
        return advertMapper.getAdvertTargetRepresentations(advertService.getAdvertTargets(userService.getCurrentUser()));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/connection/resource", method = RequestMethod.GET, params = "q")
    public List<ResourceRepresentationConnection> getConnectionResourceRepresentations(@RequestParam(required = false) String q) {
        return userMapper.getUserConnectionResourceRepresentations(userService.getCurrentUser(), q);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/filter/{resourceScope}", method = RequestMethod.PUT)
    public void saveFilter(@PathVariable String resourceScope, @RequestBody ResourceListFilterDTO filter) {
        PrismScope scope = PrismScope.valueOf(resourceScope.toUpperCase().substring(0, resourceScope.length() - 1));
        User currentUser = userService.getCurrentUser();
        try {
            resourceListFilterService.save(currentUser, entityService.getById(Scope.class, scope), filter);
        } catch (Exception e) {
            logger.info("Error saving filter for user " + currentUser.toString(), e);
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
    @RequestMapping(value = "/personalDetail", method = RequestMethod.PUT)
    public void savePersonalDetail(@Valid @RequestBody ProfilePersonalDetailDTO personalDetailDTO) {
        profileService.updatePersonalDetailUser(personalDetailDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public void saveAddress(@Valid @RequestBody ProfileAddressDTO addressDTO) {
        profileService.updateAddressUser(addressDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/qualifications", method = RequestMethod.POST)
    public Map<String, Object> createQualification(@Valid @RequestBody ProfileQualificationDTO qualificationDTO) {
        UserQualification qualification = profileService.updateQualificationUser(null, qualificationDTO);
        return ImmutableMap.of("id", (Object) qualification.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/qualifications/{qualificationId}", method = RequestMethod.PUT)
    public void updateQualification(@PathVariable Integer qualificationId, @Valid @RequestBody ProfileQualificationDTO qualificationDTO) {
        profileService.updateQualificationUser(qualificationId, qualificationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/qualifications/{qualificationId}", method = RequestMethod.DELETE)
    public void deleteQualification(@PathVariable Integer qualificationId) {
        profileService.deleteQualificationUser(qualificationId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/employmentPositions", method = RequestMethod.POST)
    public Map<String, Object> createEmploymentPosition(@Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) {
        UserEmploymentPosition employmentPosition = profileService.updateEmploymentPositionUser(null, employmentPositionDTO);
        return ImmutableMap.of("id", (Object) employmentPosition.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/employmentPositions/{employmentPositionId}", method = RequestMethod.PUT)
    public void updateEmploymentPosition(@PathVariable Integer employmentPositionId, @Valid @RequestBody ProfileEmploymentPositionDTO employmentPositionDTO) {
        profileService.updateEmploymentPositionUser(employmentPositionId, employmentPositionDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/referees", method = RequestMethod.POST)
    public Map<String, Object> createReferee(@Valid @RequestBody ProfileRefereeDTO refereeDTO) {
        UserReferee referee = profileService.updateRefereeUser(null, refereeDTO);
        return ImmutableMap.of("id", (Object) referee.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/referees/{refereeId}", method = RequestMethod.PUT)
    public void deleteReferee(@PathVariable Integer refereeId, @Valid @RequestBody ProfileRefereeDTO refereeDTO) {
        profileService.updateRefereeUser(refereeId, refereeDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/employmentPositions/{employmentPositionId}", method = RequestMethod.DELETE)
    public void deleteEmploymentPosition(@PathVariable Integer employmentPositionId) {
        profileService.deleteEmploymentPositionUser(employmentPositionId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/referees/{refereeId}", method = RequestMethod.DELETE)
    public void updateReferee(@PathVariable Integer refereeId) {
        profileService.deleteRefereeUser(refereeId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/document", method = RequestMethod.PUT)
    public void saveDocument(@Valid @RequestBody ProfileDocumentDTO documentDTO) {
        profileService.updateDocumentUser(documentDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/additionalInformation", method = RequestMethod.PUT)
    public void saveAdditionalInformation(@Valid @RequestBody ProfileAdditionalInformationDTO additionalInformationDTO) {
        profileService.updateAdditionalInformationUser(additionalInformationDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public UserProfileRepresentation getUserProfile() {
        return userMapper.getUserProfileRepresentation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/profile/share", method = RequestMethod.PUT)
    public void shareUserProfile(@RequestParam(required = true) Boolean shareProfile) {
        userAccountService.shareUserProfile(shareProfile);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "profiles", method = RequestMethod.GET)
    public List<ProfileListRowRepresentation> getUserProfiles(@RequestBody ProfileListFilterDTO filter) {
        return userMapper.getProfileListRowRepresentations(filter);
    }

}
