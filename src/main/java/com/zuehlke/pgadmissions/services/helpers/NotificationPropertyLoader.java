package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;
import com.zuehlke.pgadmissions.utils.PrismTemplateUtils;

@Service
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotificationPropertyLoader {

    private PropertyLoader propertyLoader;

    private NotificationDefinitionDTO notificationDefinitionDTO;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Value("${system.helpdesk}")
    private String helpdesk;

    @Inject
    private ActionService actionService;

    @Inject
    private SystemService systemService;

    @Inject
    private PrismTemplateUtils prismTemplateUtils;

    @Inject
    ApplicationContext applicationContext;

    public PropertyLoader getPropertyLoader() {
        return propertyLoader;
    }

    public NotificationDefinitionDTO getNotificationDefinitionDTO() {
        return notificationDefinitionDTO;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public String getApplicationApiUrl() {
        return applicationApiUrl;
    }

    public String getHelpdesk() {
        return helpdesk;
    }

    public ActionService getActionService() {
        return actionService;
    }

    public SystemService getSystemService() {
        return systemService;
    }

    public String load(PrismNotificationDefinitionProperty property) {
        return applicationContext.getBean(property.getBuilder()).build(this);
    }

    public NotificationPropertyLoader localize(NotificationDefinitionDTO notificationDefinitionDTO, PropertyLoader propertyLoader) {
        this.notificationDefinitionDTO = notificationDefinitionDTO;
        this.notificationDefinitionDTO.setSignatory(systemService.getSystem().getUser());
        this.propertyLoader = propertyLoader;
        return this;
    }

    public String getRedirectionControl(PrismDisplayPropertyDefinition linkLabel) {
        return getRedirectionControl(linkLabel, null);
    }

    public String getRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel) {
        return getRedirectionControl(url, linkLabel, null);
    }

    public String getRedirectionControl(PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel) {
        Resource resource = notificationDefinitionDTO.getResource();
        String url = getRedirectionUrl(resource, notificationDefinitionDTO.getTransitionAction(), notificationDefinitionDTO.getRecipient());
        return getRedirectionControl(url, linkLabel, declineLinkLabel);
    }

    public String getCommentAssigneesAsString(PrismRole roleId) {
        Set<CommentAssignedUser> assigneeObjects = notificationDefinitionDTO.getComment().getAssignedUsers();
        Set<String> assigneeStrings = Sets.newTreeSet();
        for (CommentAssignedUser assigneeObject : assigneeObjects) {
            if (assigneeObject.getRole().getId() == roleId && assigneeObject.getRoleTransitionType() == CREATE) {
                assigneeStrings.add(assigneeObject.getUser().getFullName());
            }
        }
        return Joiner.on(", ").join(assigneeStrings);
    }

    public String getRedirectionUrl(Integer resourceId, PrismAction actionId, User user) {
        return applicationApiUrl + "/mail/activate?resourceId=" + resourceId + "&actionId=" + actionId.name() + "&activationCode=" + user.getActivationCode();
    }

    private String getRedirectionUrl(Resource resource, PrismAction actionId, User user) {
        Resource operative = (Resource) PrismReflectionUtils.getProperty(resource, actionId.getScope().getLowerCamelName());
        return applicationApiUrl + "/mail/activate?resourceId=" + operative.getId() + "&actionId=" + actionId.name() + "&activationCode="
                + user.getActivationCode();
    }

    private String getRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel) {
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", url, "label", propertyLoader.loadLazy(linkLabel));
        model.put("link", link);

        if (declineLinkLabel != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", url + "&decline=1", "label", propertyLoader.loadLazy(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        return prismTemplateUtils.getContentFromLocation("Email Control Template", "email/email_control_template.ftl", model);
    }

}
