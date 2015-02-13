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
import com.zuehlke.pgadmissions.exceptions.PrismConflictException;
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

    public OauthUserDefinition loadUserDefinition(OauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthUserDefinition definition;
        switch (oauthProvider) {
        case FACEBOOK:
            definition = loadFacebookUserDefinition(oauthLoginDTO);
            break;
        case LINKEDIN:
            definition = loadLinkedinUserDefinition(oauthLoginDTO);
            break;
        case GOOGLE:
            definition = loadGoogleUserDefinition(oauthLoginDTO);
            break;
        case TWITTER:
            definition = loadTwitterUserDefinition(oauthLoginDTO, session);
            break;
        default:
            throw new Error("Unknown oAuth provider: " + oauthProvider);
        }
        return definition.withOauthProvider(oauthProvider);
    }

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

    private OauthUserDefinition loadFacebookUserDefinition(OauthLoginDTO oauthLoginDTO) {
        FacebookOAuth2Template facebookOAuth2Template = new FacebookOAuth2Template(oauthLoginDTO.getClientId(), facebookAppSecret);
        AccessGrant accessGrant = facebookOAuth2Template.exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        FacebookTemplate facebookTemplate = new FacebookTemplate(accessGrant.getAccessToken());
        FacebookProfile userProfile = facebookTemplate.userOperations().getUserProfile();

        return new OauthUserDefinition().withExternalId(userProfile.getId()).withEmail(userProfile.getEmail()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName());
    }

    private OauthUserDefinition loadLinkedinUserDefinition(OauthLoginDTO oauthLoginDTO) {
        LinkedInServiceProvider linkedInServiceProvider = new LinkedInServiceProvider(oauthLoginDTO.getClientId(), linkedinAppSecret);
        AccessGrant accessGrant = linkedInServiceProvider.getOAuthOperations().exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        LinkedInTemplate linkedInTemplate = new LinkedInTemplate(accessGrant.getAccessToken());
        LinkedInProfile userProfile = linkedInTemplate.profileOperations().getUserProfile();

        return new OauthUserDefinition().withExternalId(userProfile.getId()).withEmail(userProfile.getEmailAddress()).withFirstName(userProfile.getFirstName())
                .withLastName(userProfile.getLastName());
    }

    private OauthUserDefinition loadGoogleUserDefinition(OauthLoginDTO oauthLoginDTO) {
        GoogleOAuth2Template googleOAuth2Template = new GoogleOAuth2Template(oauthLoginDTO.getClientId(), googleAppSecret);
        AccessGrant accessGrant = googleOAuth2Template.exchangeForAccess(oauthLoginDTO.getCode(), oauthLoginDTO.getRedirectUri(), null);

        GoogleTemplate googleTemplate = new GoogleTemplate(accessGrant.getAccessToken());
        Person person = googleTemplate.plusOperations().getGoogleProfile();

        return new OauthUserDefinition().withExternalId(person.getId()).withEmail(person.getAccountEmail()).withFirstName(person.getGivenName())
                .withLastName(person.getFamilyName());
    }

    private OauthUserDefinition loadTwitterUserDefinition(OauthLoginDTO oauthLoginDTO, HttpSession session) {
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

    public User getOrCreateUser(OauthProvider oauthProvider, OauthLoginDTO oauthLoginDTO, HttpSession session) {
        OauthAssociationType associationType = oauthLoginDTO.getAssociationType();
        OauthUserDefinition userDefinition = loadUserDefinition(oauthProvider, oauthLoginDTO, session);
        User userByExternalAccount = userService.getByExternalAccountId(oauthProvider, userDefinition.getExternalId());

        User user;
        if (associationType == OauthAssociationType.ONLY_AUTHENTICATE) {
            if (userByExternalAccount == null) {
                throw new AccessDeniedException("Account not associated");
            }
            return userByExternalAccount;
        }

        if (associationType == OauthAssociationType.ASSOCIATE_WITH_CURRENT_USER) {
            user = Preconditions.checkNotNull(userService.getCurrentUser());
        } else if (associationType == OauthAssociationType.ASSOCIATE_WITH_PROVIDED_USER) {
            user = Preconditions.checkNotNull(userService.getUserByActivationCode(oauthLoginDTO.getActivationCode()));
        } else if (associationType == OauthAssociationType.CREATE_NEW_USER) {
            if (userByExternalAccount != null) { // user exists, no need to create
                user = userByExternalAccount;
            } else {
                if (userService.getUserByEmail(userDefinition.getEmail()) != null) {
                    throw new PrismConflictException("User with given email already exists");
                } // user details need confirmation, storing current details in session
                session.setAttribute(OAUTH_USER_TO_CONFIRM, userDefinition);
                return null;
            }
        } else {
            throw new Error("Unknown association type: " + associationType);
        }

        if (userByExternalAccount != null && !userByExternalAccount.getId().equals(user.getId())) {
            throw new AccessDeniedException("Given " + oauthProvider.getName() + " account is already associated with another account");
        }

        if (userByExternalAccount == null) { // create association (and account if not there yet)
            createUserAccount(user, oauthProvider, userDefinition.getExternalId(), null, true);
        }

        return user;
    }

    public User registerUser(UserRegistrationDTO registrationDTO, HttpSession session) throws Exception {
        Resource resource = resourceService.getById(registrationDTO.getAction().getActionId().getScope().getResourceClass(), registrationDTO.getResourceId());

        User existingUser = userService.getUserByEmail(registrationDTO.getEmail());
        boolean userAlreadyExists = existingUser != null;
        if (userAlreadyExists) { // if user has been already created means that email is already confirmed (user knew activation code), we can activate the user straight away
            if (!existingUser.getActivationCode().equals(registrationDTO.getActivationCode())) {
                throw new ResourceNotFoundException("Incorrect code");
            }
            if (existingUser.getUserAccount() != null) {
                throw new ResourceNotFoundException("User is already registered");
            }
        }

        User user = userService
                .getOrCreateUser(registrationDTO.getFirstName(), registrationDTO.getLastName(), registrationDTO.getEmail(), resource.getLocale());

        if (registrationDTO.getPassword() != null) { // regular password based registration
            createUserAccount(user, null, null, registrationDTO.getPassword(), userAlreadyExists);
        } else { // Oauth registration, user details confirmed
            OauthUserDefinition userDefinition = (OauthUserDefinition) session.getAttribute(OAUTH_USER_TO_CONFIRM);
            if (userDefinition == null) {
                throw new Error("User definition not found in session");
            }
            createUserAccount(user, userDefinition.getOauthProvider(), userDefinition.getExternalId(), null, userAlreadyExists);

        }
        ActionOutcomeDTO outcome = actionService.getRegistrationOutcome(user, registrationDTO);
        notificationService.sendRegistrationNotification(user, outcome);
        return user;
    }

    private void createUserAccount(User user, OauthProvider oauthProvider, String externalAccountId, String password, boolean enableAccount) {
        if (user.getUserAccount() == null) {
            String encryptedPassword = password != null ? EncryptionUtils.getMD5(password) : null;
            UserAccount account = new UserAccount().withSendApplicationRecommendationNotification(false).withPassword(encryptedPassword)
                    .withEnabled(enableAccount);
            user.setUserAccount(account);
            entityService.save(account);
        }

        UserAccount userAccount = user.getUserAccount();
        userAccount.setEnabled(enableAccount);
        entityService.save(user);
        if (oauthProvider != null) {
            UserAccountExternal externalAccount = new UserAccountExternal().withUserAccount(userAccount).withAccountType(oauthProvider)
                    .withAccountIdentifier(externalAccountId);
            userAccount.getExternalAccounts().add(externalAccount);
            userAccount.setPrimaryExternalAccount(externalAccount);
            entityService.save(externalAccount);
        }
    }

}
