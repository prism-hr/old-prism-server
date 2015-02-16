package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookOAuth2Template;
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
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.definitions.OauthProvider;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.dto.ActionOutcomeDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthAssociationType;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthLoginDTO;
import com.zuehlke.pgadmissions.rest.dto.auth.OauthUserDefinition;
import com.zuehlke.pgadmissions.rest.dto.user.UserRegistrationDTO;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;

@Service
@Transactional
public class AuthenticationService {

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
    private ResourceService resourceService;

    @Inject
    private EntityService entityService;

    public String requestToken(HttpSession session, OauthProvider oauthProvider) {
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

    public User getOrCreateUserAccountExternal(OauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthAssociationType oauthAssociationType = oauthLoginDTO.getAssociationType();
        OauthUserDefinition oauthUserDefinition = getOauthUserDefinition(oauthProvider, oauthLoginDTO, session);

        switch (oauthAssociationType) {
        case ASSOCIATE_CURRENT_USER:
            return oauthAssociateUser(userService.getCurrentUser(), oauthProvider, oauthUserDefinition);
        case ASSOCIATE_NEW_USER:
            return oauthAssociateNewUser(oauthProvider, oauthUserDefinition, session);
        case ASSOCIATE_SPECIFIED_USER:
            return oauthAssociateUser(userService.getUserByActivationCode(oauthLoginDTO.getActivationCode()), oauthProvider, oauthUserDefinition);
        case AUTHENTICATE:
            return oauthAuthenticate(oauthProvider, oauthUserDefinition);
        default:
            throw new UnsupportedOperationException("Unsupported Oauth association type: " + oauthAssociationType);
        }
    }

    public User registerUser(UserRegistrationDTO registrationDTO, HttpSession session) throws Exception {
        User user = userService.getUserByEmail(registrationDTO.getEmail());
        Resource resource = resourceService.getById(registrationDTO.getAction().getActionId().getScope().getResourceClass(), registrationDTO.getResourceId());

        boolean enableAccount = user != null;
        if (enableAccount) {
            if (!user.getActivationCode().equals(registrationDTO.getActivationCode())) {
                throw new ResourceNotFoundException("Incorrect activation code");
            } else if (user.getUserAccount() != null) {
                throw new ResourceNotFoundException("User already registered");
            }
        }

        user = userService.getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail(), resource.getLocale());

        if (registrationDTO.getPassword() == null) {
            OauthUserDefinition oauthUserDefinition = (OauthUserDefinition) session.getAttribute(OAUTH_USER_TO_CONFIRM);
            createUserAccount(user, oauthUserDefinition.getOauthProvider(), oauthUserDefinition.getExternalId(), null, enableAccount);
        } else {
            createUserAccount(user, null, null, registrationDTO.getPassword(), enableAccount);
        }

        ActionOutcomeDTO outcome = actionService.getRegistrationOutcome(user, registrationDTO);
        notificationService.sendRegistrationNotification(user, outcome);
        return user;
    }

    private OauthUserDefinition getOauthUserDefinition(OauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
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
        FacebookOAuth2Template facebookOAuth2Template = new FacebookOAuth2Template(oauthLoginDTO.getClientId(), facebookAppSecret);
        AccessGrant accessGrant = facebookOAuth2Template.exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        FacebookTemplate facebookTemplate = new FacebookTemplate(accessGrant.getAccessToken());
        FacebookProfile userProfile = facebookTemplate.userOperations().getUserProfile();

        return new OauthUserDefinition().withExternalId(userProfile.getId()).withEmail(userProfile.getEmail()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName());
    }

    private OauthUserDefinition getLinkedinUserDefinition(OauthLoginDTO oauthLoginDTO) {
        LinkedInServiceProvider linkedInServiceProvider = new LinkedInServiceProvider(oauthLoginDTO.getClientId(), linkedinAppSecret);
        AccessGrant accessGrant = linkedInServiceProvider.getOAuthOperations().exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        LinkedInTemplate linkedInTemplate = new LinkedInTemplate(accessGrant.getAccessToken());
        LinkedInProfile userProfile = linkedInTemplate.profileOperations().getUserProfile();

        return new OauthUserDefinition().withExternalId(userProfile.getId()).withEmail(userProfile.getEmailAddress()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName());
    }

    private OauthUserDefinition getGoogleUserDefinition(OauthLoginDTO oauthLoginDTO) {
        GoogleOAuth2Template googleOAuth2Template = new GoogleOAuth2Template(oauthLoginDTO.getClientId(), googleAppSecret);
        AccessGrant accessGrant = googleOAuth2Template.exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        GoogleTemplate googleTemplate = new GoogleTemplate(accessGrant.getAccessToken());
        Person person = googleTemplate.plusOperations().getGoogleProfile();

        return new OauthUserDefinition().withExternalId(person.getId()).withEmail(person.getAccountEmail()).withFirstName(person.getGivenName())
                .withLastName(person.getFamilyName());
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
                .withLastName(names.size() > 1 ? names.get(1) : null);
    }

    private void createUserAccount(User user, OauthProvider oauthProvider, String externalAccountId, String password, boolean enableAccount) {
        UserAccount userAccount = user.getUserAccount();
        if (userAccount == null) {
            String encryptedPassword = password != null ? EncryptionUtils.getMD5(password) : null;
            userAccount = new UserAccount().withSendApplicationRecommendationNotification(false).withPassword(encryptedPassword).withEnabled(enableAccount);
            entityService.save(userAccount);
            user.setUserAccount(userAccount);
        }

        userAccount.setEnabled(enableAccount);

        if (oauthProvider != null) {
            createExternalUserAccount(userAccount, oauthProvider, externalAccountId);
        }
    }

    private User oauthAssociateUser(User user, OauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition) {
        Preconditions.checkNotNull(user);
        User oathUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());

        if (oathUser == null) {
            createExternalUserAccount(user.getUserAccount(), oauthProvider, oauthUserDefinition.getExternalId());
        } else if (!user.getId().equals(oathUser.getId())) {
            throw new AccessDeniedException("Oauth credentials associated with another user");
        }

        return user;
    }

    private User oauthAssociateNewUser(OauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition, HttpSession session) {
        User oauthUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());
        if (oauthUser == null) {
            User user = userService.getUserByEmail(oauthUserDefinition.getEmail());
            if (user == null) {
                session.setAttribute(OAUTH_USER_TO_CONFIRM, oauthUserDefinition);
                return null;
            }
            createExternalUserAccount(user.getUserAccount(), oauthProvider, oauthUserDefinition.getExternalId());
        }
        return oauthUser;
    }

    private User oauthAuthenticate(OauthProvider oauthProvider, OauthUserDefinition oauthUserDefinition) {
        User oauthUser = userService.getByExternalAccountId(oauthProvider, oauthUserDefinition.getExternalId());
        if (oauthUser == null) {
            throw new AccessDeniedException("Oauth credentials not associated with user");
        }
        return oauthUser;
    }

    private void createExternalUserAccount(UserAccount userAccount, OauthProvider oauthProvider, String oauthAccountId) {
        if (userAccount != null) {
            UserAccountExternal externalAccount = new UserAccountExternal().withUserAccount(userAccount).withAccountType(oauthProvider)
                    .withAccountIdentifier(oauthAccountId);
            entityService.save(externalAccount);
            userAccount.getExternalAccounts().add(externalAccount);
            userAccount.setPrimaryExternalAccount(externalAccount);
        }
    }

}
