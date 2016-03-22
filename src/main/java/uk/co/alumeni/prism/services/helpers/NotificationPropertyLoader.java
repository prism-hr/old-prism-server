package uk.co.alumeni.prism.services.helpers;

import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_CANDIDATE_VIEW_PROFILE;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROCEED;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismAction.SYSTEM_MANAGE_ACCOUNT;
import static uk.co.alumeni.prism.utils.PrismReflectionUtils.getProperty;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.dto.NotificationDefinitionDTO;
import uk.co.alumeni.prism.services.ActionService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.utils.PrismTemplateUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

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

    public String getUserAccountControl() {
        return getRedirectionControl(
                getRedirectionUrl(notificationDefinitionDTO.getResource().getId(), SYSTEM_MANAGE_ACCOUNT, notificationDefinitionDTO.getRecipient()),
                SYSTEM_MANAGE_ACCOUNT.getDisplayProperty());
    }

    public String getCandidateProfileControl() {
        return getRedirectionControl(applicationApiUrl + "/mail/candidates/" + notificationDefinitionDTO.getCandidate().getId() + "/messages",
                SYSTEM_CANDIDATE_VIEW_PROFILE);
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

    public String getInvitationAcceptControl() {
        return getRedirectionControl(applicationApiUrl + "/mail/activate?resourceId=" + notificationDefinitionDTO.getResource().getId() + "&actionId="
                + notificationDefinitionDTO.getTransitionAction().name() + "&targetInvitation=" + notificationDefinitionDTO.getAdvertTarget().getId()
                + "&activationCode=" + notificationDefinitionDTO.getRecipient().getActivationCode(), SYSTEM_PROCEED);
    }

    public String getRedirectionUrl(Integer resourceId, PrismAction actionId, User user) {
        return applicationApiUrl + "/mail/activate?resourceId=" + resourceId + "&actionId=" + actionId.name() + "&activationCode=" + user.getActivationCode();
    }

    private String getRedirectionUrl(Resource resource, PrismAction actionId, User user) {
        Resource operative = (Resource) getProperty(resource, actionId.getScope().getLowerCamelName());
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
