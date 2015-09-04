package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NO_DIAGNOSTIC_INFORMATION;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccount;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.domain.user.UserFeedback;
import com.zuehlke.pgadmissions.dto.UserSelectionDTO;
import com.zuehlke.pgadmissions.rest.dto.UserListFilterDTO;
import com.zuehlke.pgadmissions.rest.representation.ScopeActionSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ScopeUpdateSummaryRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserActivityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserFeedbackRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserInstitutionIdentityRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationUnverified;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.services.UserFeedbackService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.services.helpers.PropertyLoader;

@Service
@Transactional
public class UserMapper {

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private RoleService roleService;

    @Inject
    private UserFeedbackService userFeedbackService;

    @Inject
    private UserService userService;

    @Inject
    private SystemService systemService;

    @Inject
    private ApplicationMapper applicationMapper;

    @Inject
    private ScopeMapper scopeMapper;

    @Inject
    private ApplicationContext applicationContext;

    public UserRepresentationSimple getUserRepresentationSimple(User user) {
        return getUserRepresentation(user, UserRepresentationSimple.class);
    }

    public UserRepresentationExtended getUserRepresentationExtended(User user) {
        UserRepresentationExtended representation = getUserRepresentation(user, UserRepresentationExtended.class);
        representation.setSendApplicationRecommendationNotification(user.getUserAccount().getSendApplicationRecommendationNotification());

        PrismScope permissionScope = roleService.getPermissionScope(user);
        representation.setPermissionScope(permissionScope);

        representation.setParentUser(user.getEmail());
        representation.setLinkedUsers(userService.getLinkedUserAccounts(user));

        representation.setOauthProviders(getOathProviderRepresentations(user));
        representation.setRequiredFeedbackRoleCategory(userFeedbackService.getRequiredFeedbackRoleCategory(user));
        return representation;
    }

    public List<UserRepresentationUnverified> getUserUnverifiedRepresentations(Resource resource, UserListFilterDTO filterDTO) {
        String noDiagnosis = applicationContext.getBean(PropertyLoader.class).localize(systemService.getSystem()).loadLazy(SYSTEM_NO_DIAGNOSTIC_INFORMATION);

        List<User> users = userService.getBouncedOrUnverifiedUsers(resource, filterDTO);
        List<UserRepresentationUnverified> representations = Lists.newArrayListWithCapacity(users.size());
        for (User user : users) {
            representations.add(getUserRepresentationUnverified(user, noDiagnosis));
        }

        return representations;
    }

    public UserFeedbackRepresentation getUserFeedbackRepresentation(UserFeedback userFeedback) {
        UserFeedbackRepresentation representation = new UserFeedbackRepresentation()
                .withResource(resourceMapper.getResourceRepresentationSimple(userFeedback.getResource()))
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

    public UserInstitutionIdentityRepresentation getUserInstitutionIdentityRepresentation(User user, Institution institution,
            PrismUserInstitutionIdentity identityType) {
        return new UserInstitutionIdentityRepresentation().withIdentityType(identityType).withIdentifier(
                userService.getUserInstitutionIdentity(user, institution, identityType));
    }

    private UserRepresentationUnverified getUserRepresentationUnverified(User user, String noDiagnosisMessage) {
        UserRepresentationUnverified representation = getUserRepresentation(user, UserRepresentationUnverified.class);

        String bounceMessage = user.getEmailBouncedMessage();
        if (bounceMessage != null) {
            JsonPrimitive diagnosis = new JsonParser().parse(bounceMessage).getAsJsonObject().getAsJsonPrimitive("diagnosticCode");
            representation.setDiagnosticInformation(diagnosis != null ? diagnosis.getAsString() : noDiagnosisMessage);
        }

        return representation;
    }

    public <T extends UserRepresentationSimple> T getUserRepresentation(User user, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(user.getId());
        representation.setFirstName(user.getFirstName());
        representation.setFirstName2(user.getFirstName2());
        representation.setFirstName3(user.getFirstName3());
        representation.setLastName(user.getLastName());
        representation.setFullName(user.getFullName());
        representation.setEmail(user.getEmail());

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

    public List<UserRepresentationSimple> getUserRepresentations(List<UserSelectionDTO> users) {
        List<UserRepresentationSimple> representations = Lists.newLinkedList();
        for (UserSelectionDTO user : users) {
            representations.add(getUserRepresentationSimple(user.getUser()));
        }
        return representations;
    }

    public UserActivityRepresentation getUserActivityRepresentation(User user, PrismScope permissionScope) {
        UserActivityRepresentation representation = new UserActivityRepresentation();

        List<ScopeActionSummaryRepresentation> urgentSummaries = Lists.newLinkedList();
        List<ScopeUpdateSummaryRepresentation> updateSummaries = Lists.newLinkedList();
        scopeMapper.populateScopeSummaries(user, permissionScope, urgentSummaries, updateSummaries);

        representation.setUrgentSummaries(urgentSummaries);
        representation.setUpdateSummaries(updateSummaries);

        representation.setAppointmentSummaries(applicationMapper.getApplicationAppointmentRepresentations(user));
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
