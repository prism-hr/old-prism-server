package uk.co.alumeni.prism.services;

import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_SHARED;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.PROFILE_UNSHARED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.linkedin.connect.LinkedInServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserAccountUpdate;
import uk.co.alumeni.prism.domain.workflow.Action;
import uk.co.alumeni.prism.dto.ActionOutcomeDTO;
import uk.co.alumeni.prism.exceptions.ResourceNotFoundException;
import uk.co.alumeni.prism.rest.dto.auth.OauthAssociationType;
import uk.co.alumeni.prism.rest.dto.auth.OauthLoginDTO;
import uk.co.alumeni.prism.rest.dto.auth.OauthUserDefinition;
import uk.co.alumeni.prism.rest.dto.comment.CommentDTO;
import uk.co.alumeni.prism.rest.dto.user.UserRegistrationDTO;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;
import uk.co.alumeni.prism.utils.PrismEncryptionUtils;

import com.google.common.base.Preconditions;

@Service
@Transactional
public class UserAccountService {

    public static final String OAUTH_USER_TO_CONFIRM = "oauthUserToConfirm";

    @Value("${auth.facebook.appSecret}")
    private String facebookAppSecret;

    @Value("${auth.linkedin.appSecret}")
    private String linkedinAppSecret;

    @Value("${auth.google.appSecret}")
    private String googleAppSecret;

    @Value("${auth.twitter.appSecret}")
    private String twitterAppSecret;

    @Value("${auth.twitter.clientId}")
    private String twitterClientId;

    @Value("${application.url}")
    private String applicationUrl;

    @Inject
    private ActionService actionService;

    @Inject
    private ActivityService activityService;

    @Inject
    private EntityService entityService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    @Inject
    private UserService userService;

    @Inject
    private ApplicationContext applicationContext;

    public User getOrCreateUserAccountExternal(OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthAssociationType oauthAssociationType = oauthLoginDTO.getAssociationType();
        OauthUserDefinition oauthUserDefinition = getLinkedinUserDefinition(oauthLoginDTO);

        switch (oauthAssociationType) {
        case ASSOCIATE_CURRENT_USER:
            return oauthAssociateUser(userService.getCurrentUser(), oauthUserDefinition);
        case ASSOCIATE_NEW_USER:
            return oauthAssociateNewUser(oauthUserDefinition, session);
        case ASSOCIATE_SPECIFIED_USER:
            return oauthAssociateUser(userService.getUserByActivationCode(oauthLoginDTO.getActivationCode()), oauthUserDefinition);
        case AUTHENTICATE:
            return oauthAuthenticate(oauthUserDefinition);
        default:
            throw new UnsupportedOperationException("Unsupported Oauth association type: " + oauthAssociationType);
        }
    }

    public User registerUser(UserRegistrationDTO userRegistrationDTO, HttpSession session) {
        User user = userService.getUserByEmail(userRegistrationDTO.getEmail());

        boolean enableAccount = user != null;
        if (enableAccount) {
            if (user.getUserAccount() == null && userRegistrationDTO.getActivationCode() != null) {
                if (!user.getActivationCode().equals(userRegistrationDTO.getActivationCode())) {
                    throw new Error();
                }
            } else {
                throw new ResourceNotFoundException("User is already registered");
            }
        }

        user = userService.getOrCreateUser(userRegistrationDTO.getFirstName(), userRegistrationDTO.getLastName(), userRegistrationDTO.getEmail());

        if (userRegistrationDTO.getPassword() == null) {
            OauthUserDefinition oauthUserDefinition = (OauthUserDefinition) session.getAttribute(OAUTH_USER_TO_CONFIRM);
            getOrCreateUserAccount(user, oauthUserDefinition, enableAccount);
        } else {
            getOrCreateUserAccount(user, userRegistrationDTO.getPassword(), enableAccount);
        }

        CommentDTO commentDTO = userRegistrationDTO.getComment();
        if (commentDTO != null) {
            System system = systemService.getSystem();
            Action transitionAction = actionService.getById(SYSTEM_MANAGE_ACCOUNT);
            ActionOutcomeDTO outcome = new ActionOutcomeDTO().withResource(system).withTransitionResource(system).withTransitionAction(transitionAction);
            if (commentDTO.isBypassComment()) {
                resourceService.executeActionBypass(user, commentDTO);
            } else {
                outcome = actionService.executeRegistrationAction(user, userRegistrationDTO);
            }
            notificationService.sendCompleteRegistrationRequest(user, outcome);
        }

        return user;
    }

    public void updateUserAccount(UserAccount userAccount, PrismDisplayPropertyDefinition message) {
        DateTime baseline = DateTime.now();
        userAccount.setUpdatedTimestamp(baseline);
        activityService.setSequenceIdentifier(userAccount, baseline);
        createUserAccountUpdate(userAccount, message);
    }

    public void shareUserProfile(Boolean shareProfile) {
        UserAccount userAccount = userService.getCurrentUser().getUserAccount();
        userAccount.setShared(shareProfile);
        createUserAccountUpdate(userAccount, isTrue(shareProfile) ? PROFILE_SHARED : PROFILE_UNSHARED);
    }

    public void enableUserAccount(UserAccount userAccount) {
        DateTime baseline = now();
        userAccount.setEnabled(true);
        User user = userAccount.getUser();
        user.setLastLoggedInTimestamp(now());
        user.getUserRoles().stream().forEach(userRole -> activityService.setSequenceIdentifier(userRole, baseline));
    }

    private void createUserAccountUpdate(UserAccount userAccount, PrismDisplayPropertyDefinition message) {
        UserAccountUpdate update = new UserAccountUpdate().withUserAccount(userAccount)
                .withContent(applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem()).loadLazy(message)).withCreatedTimestamp(now());
        entityService.save(update);
        userAccount.getUpdates().add(update);
        update.setSequenceIdentifier(Long.toString(update.getCreatedTimestamp().getMillis()) + String.format("%010d", update.getId()));
    }

    private OauthUserDefinition getLinkedinUserDefinition(OauthLoginDTO oauthLoginDTO) {
        LinkedInServiceProvider linkedInServiceProvider = new LinkedInServiceProvider(oauthLoginDTO.getClientId(), linkedinAppSecret);
        AccessGrant accessGrant = linkedInServiceProvider.getOAuthOperations().exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        LinkedInTemplate linkedInTemplate = new LinkedInTemplate(accessGrant.getAccessToken());
        LinkedInProfile userProfile = linkedInTemplate.profileOperations().getUserProfile();

        return new OauthUserDefinition().withLinkedinId(userProfile.getId()).withEmail(userProfile.getEmailAddress()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName()).withAccountProfileUrl(userProfile.getPublicProfileUrl())
                .withAccountImageUrl(userProfile.getProfilePictureUrl());
    }

    private User oauthAssociateUser(User user, OauthUserDefinition oauthUserDefinition) {
        Preconditions.checkNotNull(user);
        User oauthUser = userService.getByLinkedinId(oauthUserDefinition.getLinkedinId());

        if (oauthUser == null) {
            oauthUser = user;
            getOrCreateUserAccount(oauthUser, oauthUserDefinition, true);
        } else if (!user.getId().equals(oauthUser.getId())) {
            throw new AccessDeniedException("Account associated with another user.");
        }

        return oauthUser;
    }

    private User oauthAssociateNewUser(OauthUserDefinition oauthUserDefinition, HttpSession session) {
        User oauthUser = userService.getByLinkedinId(oauthUserDefinition.getLinkedinId());
        if (oauthUser == null) {
            oauthUser = userService.getUserByEmail(oauthUserDefinition.getEmail());
            if (oauthUser == null) {
                session.setAttribute(OAUTH_USER_TO_CONFIRM, oauthUserDefinition);
                return null;
            }
            updateLinkedinDetails(oauthUser.getUserAccount(), oauthUserDefinition);
        }
        return oauthUser;
    }

    private User oauthAuthenticate(OauthUserDefinition oauthUserDefinition) {
        User oauthUser = userService.getByLinkedinId(oauthUserDefinition.getLinkedinId());
        if (oauthUser == null) {
            throw new AccessDeniedException("Account is not associated with any user.");
        }
        updateLinkedinDetails(oauthUser.getUserAccount(), oauthUserDefinition);
        return oauthUser;
    }

    private void getOrCreateUserAccount(User user, String password, boolean enableAccount) {
        getOrCreateUserAccount(user, null, password, enableAccount);
    }

    private void getOrCreateUserAccount(User user, OauthUserDefinition oauthUserDefinition, boolean enableAccount) {
        getOrCreateUserAccount(user, oauthUserDefinition, null, enableAccount);
    }

    private void getOrCreateUserAccount(User user, OauthUserDefinition oauthUserDefinition, String password, boolean enableAccount) {
        UserAccount userAccount = user.getUserAccount();
        if (userAccount == null) {
            userAccount = createUserAccount(user, password, enableAccount);
        } else {
            enableUserAccount(userAccount);
        }

        if (oauthUserDefinition != null) {
            updateLinkedinDetails(userAccount, oauthUserDefinition);
        }
    }

    private void updateLinkedinDetails(UserAccount userAccount, OauthUserDefinition oauthUserDefinition) {
        userAccount.setLinkedinId(oauthUserDefinition.getLinkedinId());
        userAccount.setLinkedinProfileUrl(oauthUserDefinition.getAccountProfileUrl());
        userAccount.setLinkedinImageUrl(oauthUserDefinition.getAccountImageUrl());
    }

    private UserAccount createUserAccount(User user, String password, boolean enableAccount) {
        DateTime baseline = DateTime.now();
        String encryptedPassword = password != null ? PrismEncryptionUtils.getMD5(password) : null;
        UserAccount userAccount = new UserAccount().withSendActivityNotification(true).withPassword(encryptedPassword).withUpdatedTimestamp(baseline)
                .withEnabled(enableAccount).withShared(true);
        entityService.save(userAccount);
        user.setUserAccount(userAccount);
        if (enableAccount) {
            user.setLastLoggedInTimestamp(now());
        }
        return userAccount.withSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", userAccount.getId()));
    }

}
