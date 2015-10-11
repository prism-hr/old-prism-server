package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HELPDESK_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_PROPERTY_ERROR;
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
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.ActionService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.utils.PrismReflectionUtils;

import freemarker.template.Template;

@Service
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotificationPropertyLoader {

    private PropertyLoader propertyLoader;

    private NotificationDefinitionModelDTO notificationDefinitionModelDTO;

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
    private FreeMarkerConfig freemarkerConfig;

    @Inject
    ApplicationContext applicationContext;

    public PropertyLoader getPropertyLoader() {
        return propertyLoader;
    }

    public NotificationDefinitionModelDTO getNotificationDefinitionModelDTO() {
        return notificationDefinitionModelDTO;
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

    public FreeMarkerConfig getFreemarkerConfig() {
        return freemarkerConfig;
    }

    public String load(PrismNotificationDefinitionProperty property) throws Exception {
        String value = applicationContext.getBean(property.getBuilder()).build(this);
        return value == null ? "[" + propertyLoader.loadLazy(SYSTEM_NOTIFICATION_PROPERTY_ERROR) + ". " + propertyLoader.loadLazy(SYSTEM_HELPDESK_REPORT)
                + ": " + helpdesk + "]" : value;
    }

    public NotificationPropertyLoader localize(NotificationDefinitionModelDTO templateModelDTO, PropertyLoader propertyLoader) {
        this.notificationDefinitionModelDTO = templateModelDTO;
        Comment comment = this.notificationDefinitionModelDTO.getComment();
        if (comment == null) {
            this.notificationDefinitionModelDTO.setInvoker(systemService.getSystem().getUser());
        } else {
            this.notificationDefinitionModelDTO.setInvoker(comment.getUser());
        }
        this.propertyLoader = propertyLoader;
        return this;
    }

    public String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel) throws Exception {
        return buildRedirectionControl(linkLabel, null);
    }

    public String buildRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel) throws Exception {
        return buildRedirectionControl(url, linkLabel, null);
    }

    public String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel) throws Exception {
        Resource resource = notificationDefinitionModelDTO.getResource();
        String url = buildRedirectionUrl(resource, notificationDefinitionModelDTO.getTransitionAction(), notificationDefinitionModelDTO.getUser());
        return buildRedirectionControl(url, linkLabel, declineLinkLabel);
    }

    public String getCommentAssigneesAsString(PrismRole roleId) {
        Set<CommentAssignedUser> assigneeObjects = notificationDefinitionModelDTO.getComment().getAssignedUsers();
        Set<String> assigneeStrings = Sets.newTreeSet();
        for (CommentAssignedUser assigneeObject : assigneeObjects) {
            if (assigneeObject.getRole().getId() == roleId && assigneeObject.getRoleTransitionType() == CREATE) {
                assigneeStrings.add(assigneeObject.getUser().getFullName());
            }
        }
        return Joiner.on(", ").join(assigneeStrings);
    }

    public String buildRedirectionUrl(Resource resource, PrismAction actionId, User user) {
        Resource operative = (Resource) PrismReflectionUtils.getProperty(resource, actionId.getScope().getLowerCamelName());
        return applicationApiUrl + "/mail/activate?resourceId=" + operative.getId() + "&actionId=" + actionId.name() + "&activationCode="
                + user.getActivationCode();
    }

    private String buildRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel)
            throws Exception {
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", url, "label", propertyLoader.loadLazy(linkLabel));
        model.put("link", link);

        if (declineLinkLabel != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", url + "&decline=1", "label", propertyLoader.loadLazy(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        String emailControlTemplate = Resources.toString(Resources.getResource("email/email_control_template.ftl"), Charsets.UTF_8);
        Template template = new Template("Email Control Template", emailControlTemplate, freemarkerConfig.getConfiguration());

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

}
