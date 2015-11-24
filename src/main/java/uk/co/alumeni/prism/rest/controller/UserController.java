package uk.co.alumeni.prism.rest.controller;

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

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserEmploymentPosition;
import uk.co.alumeni.prism.domain.user.UserQualification;
import uk.co.alumeni.prism.domain.user.UserReferee;
import uk.co.alumeni.prism.domain.workflow.Scope;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.mapping.ProfileMapper;
import uk.co.alumeni.prism.mapping.UserMapper;
import uk.co.alumeni.prism.rest.dto.profile.ProfileAdditionalInformationDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileAddressDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileDocumentDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileEmploymentPositionDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfilePersonalDetailDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileQualificationDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileRefereeDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceListFilterDTO;
import uk.co.alumeni.prism.rest.dto.user.UserAccountDTO;
import uk.co.alumeni.prism.rest.dto.user.UserActivateDTO;
import uk.co.alumeni.prism.rest.dto.user.UserEmailDTO;
import uk.co.alumeni.prism.rest.dto.user.UserLinkingDTO;
import uk.co.alumeni.prism.rest.representation.profile.ProfileEmploymentPositionRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileQualificationRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRefereeRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationConnection;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserProfileRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationExtended;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.rest.validation.UserLinkingValidator;
import uk.co.alumeni.prism.rest.validation.UserRegistrationValidator;
import uk.co.alumeni.prism.security.AuthenticationTokenHelper;
import uk.co.alumeni.prism.services.EntityService;
import uk.co.alumeni.prism.services.ProfileService;
import uk.co.alumeni.prism.services.ResourceListFilterService;
import uk.co.alumeni.prism.services.UserAccountService;
import uk.co.alumeni.prism.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
    private ProfileMapper profileMapper;

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
    public void setParentUser(@RequestBody UserEmailDTO email) {
        userService.setParentUser(email.getEmail());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/switch", method = RequestMethod.POST)
    public Map<String, String> switchUser(@RequestParam String username) {
        User currentUser = userService.getCurrentUser();
        User user = userService.getUserByEmail(username);
        List<User> linkedUsers = userService.getLinkedUsers(currentUser);
        if (!linkedUsers.contains(user)) {
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
            userService.enableUser(user.getId());
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

    @PreAuthorize("permitAll")
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
    @RequestMapping(value = "/qualifications", method = RequestMethod.GET)
    public List<ProfileQualificationRepresentation> getQualifications() {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        return profileMapper.getQualificationRepresentations(userAccount.getQualifications());
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
    @RequestMapping(value = "/employmentPositions", method = RequestMethod.GET)
    public List<ProfileEmploymentPositionRepresentation> getEmploymentPositions() {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        return profileMapper.getEmploymentPositionRepresentations(userAccount.getEmploymentPositions());
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
    @RequestMapping(value = "/referees", method = RequestMethod.GET)
    public List<ProfileRefereeRepresentation> getReferees() {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        return profileMapper.getRefereeRepresentations(userAccount.getReferees());
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
    @RequestMapping(value = "/profile/{operation:share|hide}", method = RequestMethod.POST)
    public void shareUserProfile(@PathVariable String operation, @RequestBody Map<?, ?> undertow) {
        boolean share = operation.equals("share");
        userAccountService.shareUserProfile(share);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "profiles", method = RequestMethod.GET)
    public List<ProfileListRowRepresentation> getUserProfiles(@RequestBody ProfileListFilterDTO filter) {
        return userMapper.getProfileListRowRepresentations(filter);
    }

}
