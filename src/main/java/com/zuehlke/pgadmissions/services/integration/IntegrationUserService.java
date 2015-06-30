package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.user.UserExtendedRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserFeedbackRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationUnverified;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserFeedbackService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class IntegrationUserService {

    @Inject
    private IntegrationResourceService integrationResourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @Inject
    private UserService userService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationContext applicationContext;

    public UserRepresentationSimple getUserRepresentationSimple(User user) {
        UserRepresentationSimple representation = new UserRepresentationSimple().withId(user.getId()).withFirstName(user.getFirstName())
                .withFirstName2(user.getFirstName2()).withFirstName3(user.getEmail());

        UserAccount userAccount = user.getUserAccount();
        if (userAccount != null) {
            UserAccountExternal userAccountExternal = userAccount.getPrimaryExternalAccount();
            if (userAccountExternal != null) {
                representation.setAccountProfileUrl(userAccountExternal.getAccountProfileUrl());
                representation.setAccountImageUrl(userAccountExternal.getAccountImageUrl());
            }

            Document portraitImage = userAccount.getPortraitImage();
            representation.setPortraitImageId(portraitImage == null ? null : portraitImage.getId());
        }

        return representation;
    }

    public UserExtendedRepresentation getUserRepresentationExtended(User user) {
        UserExtendedRepresentation representation = (UserExtendedRepresentation) getUserRepresentationSimple(user);

        representation.setSendApplicationRecommendationNotification(user.getUserAccount().getSendApplicationRecommendationNotification());
        representation.setPermissionPrecedence(roleService.getPermissionPrecedence(user));

        representation.setParentUser(user.getEmail());
        representation.setLinkedUsers(userService.getLinkedUserAccounts(user));

        representation.setOauthProviders(getOathProviderRepresentations(user));
        representation.setRequiredFeedbackRoleCategory(userFeedbackService.getRequiredFeedbackRoleCategory(user));
        return representation;
    }

    public List<UserRepresentationUnverified> getUserUnverifiedRepresentations(UserListFilterDTO filterDTO) {
        List<User> users = userService.getBouncedOrUnverifiedUsers(filterDTO);
        List<UserRepresentationUnverified> representations = Lists.newArrayListWithCapacity(users.size());
        String noDiagnosis = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem()).load(SYSTEM_NO_DIAGNOSTIC_INFORMATION);

        for (User user : users) {
            representations.add(getUserRepresentationUnverified(user, noDiagnosis));
        }

        return representations;
    }

    public UserFeedbackRepresentation getUserFeedbackRepresentation(UserFeedback userFeedback) {
        UserFeedbackRepresentation representation = new UserFeedbackRepresentation()
                .withResource(integrationResourceService.getResourceRepresentationSimple(userFeedback.getResource()))
                .withUser(getUserRepresentationSimple(userFeedback.getUser())).withRoleCategory(userFeedback.getRoleCategory());

        if (BooleanUtils.isFalse(userFeedback.getDeclinedResponse())) {
            representation.setRating(userFeedback.getRating());
            representation.setContent(userFeedback.getContent());
            representation.setFeatureRequest(userFeedback.getFeatureRequest());
            representation.setRecommended(userFeedback.getRecommended());
            representation.setCreatedTimestamp(userFeedback.getCreatedTimestamp());
        }

        return representation;
    }

    private UserRepresentationUnverified getUserRepresentationUnverified(User user, String noDiagnosisMessage) {
        UserRepresentationUnverified representation = (UserRepresentationUnverified) getUserRepresentationSimple(user);

        String bounceMessage = user.getEmailBouncedMessage();
        if (bounceMessage != null) {
            JsonPrimitive diagnosis = new JsonParser().parse(bounceMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
            representation.setDiagnosticInformation(diagnosis != null ? diagnosis.getAsString() : noDiagnosisMessage);
        }

        return representation;
    }

    private List<String> getOathProviderRepresentations(User user) {
        Set<UserAccountExternal> externalAccounts = user.getUserAccount().getExternalAccounts();
        List<String> oauthProviders = Lists.newArrayListWithCapacity(externalAccounts.size());
        for (UserAccountExternal externalAccount : externalAccounts) {
            oauthProviders.add(externalAccount.getAccountType().getName());
        }
        return oauthProviders;
    }

}
