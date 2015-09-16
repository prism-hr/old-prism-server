package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.plus.Person;
import org.springframework.social.google.connect.GoogleOAuth2Template;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.linkedin.connect.LinkedInServiceProvider;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.twitter.api.TwitterProfile;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthAssociationType;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthUserDefinition;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.utils.PrismEncryptionUtils;

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

    public User getOrCreateUserAccountExternal(PrismOauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthAssociationType oauthAssociationType = oauthLoginDTO.getAssociationType();
        OauthUserDefinition oauthUserDefinition = getOauthUserDefinition(oauthProvider, oauthLoginDTO, session);

        switch (oauthAssociationType) {
        case ASSOCIATE_CURRENT_USER:
            return oauthAssociateUser(userService.getCurrentUser(), oauthProvider, oauthUserDefinition, oauthLoginDTO.getShareProfile());
        case ASSOCIATE_NEW_USER:
            return oauthAssociateNewUser(oauthProvider, oauthUserDefinition, session);
        case ASSOCIATE_SPECIFIED_USER:
            return oauthAssociateUser(userService.getUserByActivationCode(oauthLoginDTO.getActivationCode()), oauthProvider, oauthUserDefinition, oauthLoginDTO.getShareProfile());
        case AUTHENTICATE:
            return oauthAuthenticate(oauthProvider, oauthUserDefinition);
        default:
            throw new UnsupportedOperationException("Unsupported Oauth association type: " + oauthAssociationType);
        }
    }

    public User registerUser(UserRegistrationDTO registrationDTO, HttpSession session) {
        User user = userService.getUserByEmail(registrationDTO.getEmail());

        boolean enableAccount = user != null;
        if (enableAccount) {
            if (user.getUserAccount() == null && registrationDTO.getActivationCode() != null) {
                // account not activated and activation code provided
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
            getOrCreateUserAccount(user, oauthUserDefinition, enableAccount, registrationDTO.getShareProfile());
        } else {
            getOrCreateUserAccount(user, registrationDTO.getPassword(), enableAccount, registrationDTO.getShareProfile());
        }

        ActionOutcomeDTO outcome = actionService.executeRegistrationAction(user, registrationDTO);
        if (outcome != null) {
            notificationService.sendRegistrationNotification(user, outcome);
        }
        return user;
    }

    public UserAccountExternal unlinkExternalAccount(PrismOauthProvider oauthProvider) {
        User currentUser = userService.getCurrentUser();
        UserAccount userAccount = currentUser.getUserAccount();
        Set<UserAccountExternal> externalAccounts = userAccount.getExternalAccounts();

        UserAccountExternal accountToUnlink = null;
        for (UserAccountExternal externalAccount : externalAccounts) {
            if (externalAccount.getAccountType() == oauthProvider) {
                accountToUnlink = externalAccount;
            }
        }

        if (accountToUnlink == null) {
            throw new PrismForbiddenException("Account is not associated with given provider.");
        }

        externalAccounts.remove(accountToUnlink);
        if (userAccount.getPrimaryExternalAccount().getId().equals(accountToUnlink.getId())) {
            userAccount.setPrimaryExternalAccount(Iterables.getFirst(externalAccounts, null));
        }

        entityService.delete(accountToUnlink);
        return userAccount.getPrimaryExternalAccount();
    }

    public void updateUserAccount(UserAccount userAccount) {
        DateTime baseline = DateTime.now();
        userAccount.setUpdatedTimestamp(baseline);
        userAccount.setSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", userAccount.getId()));
    }

    public UserAccount getCurrentUserAccount(Integer userId) {
        User user = userService.getCurrentUser();
        if (user.getId().equals(userId)) {
            return user.getUserAccount();
        }
        throw new BadCredentialsException("Unauthorized access attempt");
    }

    public void shareUserProfile(Integer userId, Boolean shareProfile) {
        getCurrentUserAccount(userId).setShared(shareProfile);
    }

    private OauthUserDefinition getOauthUserDefinition(PrismOauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthUserDefinition definition;
        switch (oauthProvider) {
        case FACEBOOK:
            definition = getFacebookUserDefinition(oauthLoginDTO);
            break;
        case LINKEDIN:
            definition = getLinkedinUserDefinition(oauthLoginDTO);
            break;
        case GOOGLE:
            definition = getGoogleUserDefinition(oauthLoginDTO);
            break;
        case TWITTER:
            definition = getTwitterUserDefinition(oauthLoginDTO, session);
            break;
        default:
            throw new UnsupportedOperationException("Unknown Oauth provider: " + oauthProvider);
        }
        return definition.withOauthProvider(oauthProvider);
    }

    private OauthUserDefinition getFacebookUserDefinition(OauthLoginDTO oauthLoginDTO) {
        FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider(oauthLoginDTO.getClientId(), facebookAppSecret, null);
        AccessGrant accessGrant = facebookServiceProvider.getOAuthOperations().exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        FacebookTemplate facebookTemplate = new FacebookTemplate(accessGrant.getAccessToken());
        org.springframework.social.facebook.api.User userProfile = facebookTemplate.userOperations().getUserProfile();
        String userId = userProfile.getId();

        String profileUrl = "https://www.facebook.com/" + userId;
        String imageUrl = "https://graph.facebook.com/" + userId + "/picture";

        return new OauthUserDefinition().withExternalId(userId).withEmail(userProfile.getEmail()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName()).withAccountProfileUrl(profileUrl).withAccountImageUrl(imageUrl);
    }

    private OauthUserDefinition getLinkedinUserDefinition(OauthLoginDTO oauthLoginDTO) {
        LinkedInServiceProvider linkedInServiceProvider = new LinkedInServiceProvider(oauthLoginDTO.getClientId(), linkedinAppSecret);
        AccessGrant accessGrant = linkedInServiceProvider.getOAuthOperations().exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        LinkedInTemplate linkedInTemplate = new LinkedInTemplate(accessGrant.getAccessToken());
        LinkedInProfile userProfile = linkedInTemplate.profileOperations().getUserProfile();

        return new OauthUserDefinition().withExternalId(userProfile.getId()).withEmail(userProfile.getEmailAddress()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName()).withAccountProfileUrl(userProfile.getPublicProfileUrl())
                .withAccountImageUrl(userProfile.getProfilePictureUrl());
    }

    private OauthUserDefinition getGoogleUserDefinition(OauthLoginDTO oauthLoginDTO) {
        GoogleOAuth2Template googleOAuth2Template = new GoogleOAuth2Template(oauthLoginDTO.getClientId(), googleAppSecret);
        AccessGrant accessGrant = googleOAuth2Template.exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        GoogleTemplate googleTemplate = new GoogleTemplate(accessGrant.getAccessToken());
        Person person = googleTemplate.plusOperations().getGoogleProfile();

        return new OauthUserDefinition().withExternalId(person.getId()).withEmail(person.getAccountEmail()).withFirstName(person.getGivenName())
                .withLastName(person.getFamilyName()).withAccountProfileUrl(person.getUrl()).withAccountImageUrl(person.getImageUrl());
    }

    private OauthUserDefinition getTwitterUserDefinition(OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OAuthToken requestToken = (OAuthToken) session.getAttribute(OAUTH_TOKEN_ATTRIBUTE);
        session.removeAttribute(OAUTH_TOKEN_ATTRIBUTE);

        TwitterServiceProvider twitterServiceProvider = new TwitterServiceProvider(twitterClientId, twitterAppSecret);
        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, oauthLoginDTO.getOauthVerifier());
        OAuthToken oAuthToken = twitterServiceProvider.getOAuthOperations().exchangeForAccessToken(authorizedRequestToken, null);

        TwitterTemplate twitterTemplate = new TwitterTemplate(twitterClientId, twitterAppSecret, oAuthToken.getValue(), oAuthToken.getSecret());
        TwitterProfile profile = twitterTemplate.userOperations().getUserProfile();
        ArrayList<String> names = Lists.newArrayList(Splitter.on("\\s+").limit(2).split(profile.getName()));

        return new OauthUserDefinition().withExternalId(Long.toString(profile.getId())).withFirstName(names.get(0))
                .withLastName(names.size() > 1 ? names.get(1) : null).withAccountProfileUrl(profile.getProfileUrl())
                .withAccountImageUrl(profile.getProfileImageUrl());
    }

    private User oauthAssociateUser(User user, PrismOauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition, boolean shareProfile) {
        Preconditions.checkNotNull(user);
        User oauthUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());

        if (oauthUser == null) {
            oauthUser = user;
            getOrCreateUserAccount(oauthUser, oauthUserDefinition, true, shareProfile);
        } else if (!user.getId().equals(oauthUser.getId())) {
            throw new AccessDeniedException("Account associated with another user.");
        }

        return oauthUser;
    }

    private User oauthAssociateNewUser(PrismOauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition, HttpSession session) {
        User oauthUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());
        if (oauthUser == null) {
            oauthUser = userService.getUserByEmail(oauthUserDefinition.getEmail());
            if (oauthUser == null) {
                session.setAttribute(OAUTH_USER_TO_CONFIRM, oauthUserDefinition);
                return null;
            }
            createExternalUserAccount(oauthUser.getUserAccount(), oauthUserDefinition);
        }
        return oauthUser;
    }

    private User oauthAuthenticate(PrismOauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition) {
        User oauthUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());
        if (oauthUser == null) {
            throw new AccessDeniedException("Account is not associated with any user.");
        }
        createOrUpdateUserAccountExternal(oauthUser.getUserAccount(), oauthUserDefinition);
        return oauthUser;
    }

    private void getOrCreateUserAccount(User user, String password, boolean enableAccount, boolean shareProfile) {
        getOrCreateUserAccount(user, null, password, enableAccount, shareProfile);
    }

    private void getOrCreateUserAccount(User user, OauthUserDefinition oauthUserDefinition, boolean enableAccount, boolean shareProfile) {
        getOrCreateUserAccount(user, oauthUserDefinition, null, enableAccount, shareProfile);
    }

    private void getOrCreateUserAccount(User user, OauthUserDefinition oauthUserDefinition, String password, boolean enableAccount, boolean shareProfile) {
        UserAccount userAccount = user.getUserAccount();
        if (userAccount == null) {
            userAccount = createUserAccount(user, password, enableAccount, shareProfile);
        } else {
            userAccount.setEnabled(enableAccount);
        }

        if (oauthUserDefinition != null) {
            createExternalUserAccount(userAccount, oauthUserDefinition);
        }
    }

    private UserAccount createUserAccount(User user, String password, boolean enableAccount, boolean shareProfile) {
        DateTime baseline = DateTime.now();
        String encryptedPassword = password != null ? PrismEncryptionUtils.getMD5(password) : null;
        UserAccount userAccount = new UserAccount().withSendApplicationRecommendationNotification(false).withPassword(encryptedPassword).withUpdatedTimestamp(baseline)
                .withEnabled(enableAccount).withShared(shareProfile);
        entityService.save(userAccount);
        user.setUserAccount(userAccount);
        return userAccount.withSequenceIdentifier(Long.toString(baseline.getMillis()) + String.format("%010d", userAccount.getId()));
    }

    private void createExternalUserAccount(UserAccount userAccount, OauthUserDefinition oauthUserDefinition) {
        UserAccountExternal externalAccount = createOrUpdateUserAccountExternal(userAccount, oauthUserDefinition);
        userAccount.getExternalAccounts().add(externalAccount);
        userAccount.setPrimaryExternalAccount(externalAccount);
    }

    private UserAccountExternal createOrUpdateUserAccountExternal(UserAccount userAccount, OauthUserDefinition oauthUserDefinition) {
        return entityService.createOrUpdate(new UserAccountExternal().withUserAccount(userAccount).withAccountType(oauthUserDefinition.getOauthProvider())
                .withAccountIdentifier(oauthUserDefinition.getExternalId()).withAccountProfileUrl(oauthUserDefinition.getAccountProfileUrl())
                .withAccountImageUrl(oauthUserDefinition.getAccountImageUrl()));
    }

}
