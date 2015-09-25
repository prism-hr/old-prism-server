package com.zuehlke.pgadmissions.services;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthAssociationType;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthUserDefinition;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.utils.PrismEncryptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.linkedin.connect.LinkedInServiceProvider;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

@Service
@Transactional
public class UserAccountService {

    public static final String OAUTH_USER_TO_CONFIRM = "oauthUserToConfirm";

    private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

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
    private UserService userService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ActionService actionService;

    @Inject
    private EntityService entityService;

    public String requestToken(HttpSession session, PrismOauthProvider oauthProvider) {
        switch (oauthProvider) {
            case TWITTER:
                TwitterServiceProvider twitterServiceProvider = new TwitterServiceProvider(twitterClientId, twitterAppSecret);
                OAuth1Operations oAuthOperations = twitterServiceProvider.getOAuthOperations();
                OAuthToken requestToken = oAuthOperations.fetchRequestToken(applicationUrl, null);
                session.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken);
                return oAuthOperations.buildAuthorizeUrl(requestToken.getValue(), OAuth1Parameters.NONE);
            default:
                throw new Error("Requesting token not supported for: " + oauthProvider);
        }
    }

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

    public User registerUser(UserRegistrationDTO registrationDTO, HttpSession session) {
        User user = userService.getUserByEmail(registrationDTO.getEmail());

        boolean enableAccount = user != null;
        if (enableAccount) {
            if (user.getUserAccount() == null && registrationDTO.getActivationCode() != null) {
                if (!user.getActivationCode().equals(registrationDTO.getActivationCode())) {
                    throw new Error();
                }
            } else {
                throw new ResourceNotFoundException("User is already registered");
            }
        }

        user = userService.getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail());

        if (registrationDTO.getPassword() == null) {
            OauthUserDefinition oauthUserDefinition = (OauthUserDefinition) session.getAttribute(OAUTH_USER_TO_CONFIRM);
            getOrCreateUserAccount(user, oauthUserDefinition, enableAccount);
        } else {
            getOrCreateUserAccount(user, registrationDTO.getPassword(), enableAccount);
        }

        ActionOutcomeDTO outcome = actionService.executeRegistrationAction(user, registrationDTO);
        if (outcome != null) {
            notificationService.sendRegistrationNotification(user, outcome);
        }
        return user;
    }

    public void updateUserAccount(UserAccount userAccount) {
        DateTime baseline = DateTime.now();
        userAccount.setUpdatedTimestamp(baseline);
        userAccount.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", userAccount.getId()));
    }

    public void shareUserProfile(Boolean shareProfile) {
        userService.getCurrentUser().getUserAccount().setShared(shareProfile);
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
            userAccount.setEnabled(enableAccount);
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
        UserAccount userAccount = new UserAccount().withSendApplicationRecommendationNotification(false).withPassword(encryptedPassword).withUpdatedTimestamp(baseline)
                .withEnabled(enableAccount).withShared(false);
        entityService.save(userAccount);
        user.setUserAccount(userAccount);
        return userAccount.withSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", userAccount.getId()));
    }

}
