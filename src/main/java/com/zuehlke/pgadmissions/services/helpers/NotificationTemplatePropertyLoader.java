package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_DIRECTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_ACTIVATE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_COMMENT_CONTENT_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_DATE_TIME_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_DECLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_HELPDESK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_NEW_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_PROCEED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_USER_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_VALUE_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayProperty;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationTemplateModelDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.SystemService;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotificationTemplatePropertyLoader {

    private PropertyLoader propertyLoader;

    private NotificationTemplateModelDTO templateModelDTO;

    @Value("${email.control.template}")
    private String emailControlTemplateLocation;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private SystemService systemService;
    
    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    @Autowired
    private ApplicationContext applicationContext;

    public String getTemplateUserFullName() {
        return templateModelDTO.getUser().getFullName();
    }

    public String getTemplateUserFirstName() {
        return templateModelDTO.getUser().getFirstName();
    }

    public String getTemplateUserLastName() {
        return templateModelDTO.getUser().getLastName();
    }

    public String getTemplateUserEmail() {
        return templateModelDTO.getUser().getEmail();
    }

    public String getTemplateUserActivationCode() {
        return templateModelDTO.getUser().getActivationCode();
    }

    public String getTemplateAuthorFullName() {
        return templateModelDTO.getAuthor().getFullName();
    }

    public String getTemplateAuthorEmail() {
        return templateModelDTO.getAuthor().getEmail();
    }

    public String getTemplateInvokerFullName() {
        return templateModelDTO.getInvoker().getFullName();
    }

    public String getTemplateInvokerEmail() {
        return templateModelDTO.getInvoker().getEmail();
    }

    public String getTemplateSystemTitle() {
        return templateModelDTO.getResource().getSystem().getTitle();
    }

    public String getTemplateSystemHomepage() {
        return templateModelDTO.getResource().getSystem().getHomepage();
    }

    public String getTemplateHelpdesk() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getHelpdeskDisplay(), SYSTEM_HELPDESK);
    }

    public String getTemplateViewEdit() throws IOException, TemplateException {
        Resource resource = templateModelDTO.getResource();
        PrismScope scope = resource.getResourceScope() == SYSTEM ? APPLICATION : resource.getResourceScope();
        String url = resource.getSystem().getHomepage() + "/#/" + scope.getLowerCaseName() + "/" + resource.getId() + "/view?activationCode="
                + templateModelDTO.getUser().getActivationCode();
        return buildRedirectionControl(url, SYSTEM_VIEW_EDIT);
    }

    public String getActionComplete() throws IOException, TemplateException {
        Resource resource = templateModelDTO.getResource();
        String url = resource.getSystem().getHomepage() + "/#/" + resource.getResourceScope().getLowerCaseName();
        if (resource.getResourceScope() != SYSTEM) {
            url += "/" + resource.getId();
        }

        PrismAction action = templateModelDTO.getTransitionAction();

        url += "/timeline?action=" + action.name() + "&activationCode=" + templateModelDTO.getUser().getActivationCode();
        String declineUrl = action.isDeclinableAction() ? url + "&decline=true" : null;
        return buildRedirectionControl(url, SYSTEM_PROCEED, declineUrl, SYSTEM_DECLINE);
    }

    public String getCommentContent() {
        String content = templateModelDTO.getComment().getContent();
        return content == null ? propertyLoader.load(SYSTEM_COMMENT_CONTENT_NOT_PROVIDED) : "\"" + content + "\"";
    }
    
    public String getCommentDateTime() {
        return templateModelDTO.getComment().getCreatedTimestampDisplay(propertyLoader.load(SYSTEM_DATE_TIME_FORMAT));
    }
    
    public String getCommentTransitionOutcome() {
        String resourceName = templateModelDTO.getResource().getResourceScope().name();
        String statePostfix = Iterables.getLast(Lists.newArrayList(templateModelDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.load(PrismDisplayProperty.valueOf(resourceName + "_" + statePostfix));
    }

    public String getApplicationCreatorFullName() {
        return templateModelDTO.getResource().getApplication().getUser().getFullName();
    }

    public String getApplicationCode() {
        return templateModelDTO.getResource().getApplication().getCode();
    }

    public String getApplicationProjectOrProgramTitle() {
        return templateModelDTO.getResource().getApplication().getProjectOrProgramTitleDisplay();
    }

    public String getApplicationProjectOrProgramCode() {
        return templateModelDTO.getResource().getApplication().getProjectOrProgramCodeDisplay();
    }

    public String getApplicationProgramType() {
        PrismProgramType programType = PrismProgramType.valueOf(templateModelDTO.getResource().getApplication().getProgram().getProgramType().getCode());
        return propertyLoader.load(programType.getDisplayProperty());
    }

    public String getApplicationInterviewDateTime() {
        return templateModelDTO.getComment().getInterviewDateTimeDisplay(propertyLoader.load(SYSTEM_DATE_TIME_FORMAT));
    }

    public String getApplicationInterviewTimeZone() {
        return templateModelDTO.getComment().getInterviewTimeZoneDisplay();
    }

    public String getApplicationInterviewerInstructions() {
        String instructions = templateModelDTO.getComment().getInterviewerInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationIntervieweeInstructions() {
        String instructions = templateModelDTO.getComment().getIntervieweeInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationInterviewLocation() throws IOException, TemplateException {
        Comment comment = templateModelDTO.getComment();
        if (comment.getInterviewLocation() == null) {
            return "<p>" + propertyLoader.load(APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED) + "</p>";
        }
        return buildRedirectionControl(comment.getInterviewLocation(), APPLICATION_COMMENT_DIRECTIONS);
    }

    public String getApplicationConfirmedPositionTitle() {
        return templateModelDTO.getComment().getPositionTitle();
    }

    public String getApplicationConfirmedPositionDescription() {
        return templateModelDTO.getComment().getPositionDescription();
    }

    public String getApplicationConfirmedStartDate() {
        return templateModelDTO.getComment().getPositionProvisionalStartDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT));
    }

    public String getConfirmedPrimarySupervisor() {
        return getConfirmedAssignedUser(APPLICATION_PRIMARY_SUPERVISOR);
    }

    public String getConfirmedSecondarySupervisor() {
        return getConfirmedAssignedUser(APPLICATION_SECONDARY_SUPERVISOR);
    }

    public String getConfirmedOfferConditions() {
        return templateModelDTO.getComment().getAppointmentConditions();
    }

    public String getApplicationRejectionReason() {
        return templateModelDTO.getComment().getRejectionReasonDisplay();
    }

    public String getProjectTitle() {
        return templateModelDTO.getResource().getProject().getTitle();
    }

    public String getProjectCode() {
        return templateModelDTO.getResource().getProject().getCode();
    }

    public String getProgramTitle() {
        return templateModelDTO.getResource().getProgram().getTitle();
    }

    public String getProgramCode() {
        return templateModelDTO.getResource().getProgram().getCode();
    }

    public String getInstitutionTitle() {
        return templateModelDTO.getResource().getInstitution().getTitle();
    }

    public String getInstitutionCode() {
        return templateModelDTO.getResource().getInstitution().getCode();
    }

    public String getInstitutionHomepage() {
        return templateModelDTO.getResource().getInstitution().getHomepage();
    }

    public String getInstitutionDataImportError() {
        return templateModelDTO.getDataImportErrorMessage();
    }

    public String getSystemApplicationHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getSystem().getHomepage() + "/#/applications", SYSTEM_APPLICATION_LIST);
    }

    public String getSystemApplicationRecommendation() {
        return advertService.getRecommendedAdvertsForEmail(templateModelDTO.getUser());
    }

    public String getSystemProjectHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getSystem().getHomepage() + "/#/projects", SYSTEM_PROJECT_LIST);
    }

    public String getSystemProgramHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getSystem().getHomepage() + "/#/programs", SYSTEM_PROGRAM_LIST);
    }

    public String getSystemInstitutionHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getSystem().getHomepage() + "/#/institutions", SYSTEM_INSTITUTION_LIST);
    }

    public String getSystemUserNewPassword() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getNewPassword(), SYSTEM_NEW_PASSWORD);
    }

    public String getSystemUserAccountManagement() throws IOException, TemplateException {
        return buildRedirectionControl(templateModelDTO.getResource().getSystem().getHomepage() + "/#/account", SYSTEM_USER_ACCOUNT);
    }

    public String getSystemUserAccountActivation() throws IOException, TemplateException {
        Resource resource = templateModelDTO.getResource();
        User user = templateModelDTO.getUser();
        PrismAction action = templateModelDTO.getTransitionAction();
        String url = resource.getSystem().getHomepage() + "/#/activate?activationCode=" + user.getActivationCode() + "&resourceId=" + resource.getId()
                + "&action=" + action.name();
        return buildRedirectionControl(url, SYSTEM_ACTIVATE_ACCOUNT);
    }

    public NotificationTemplatePropertyLoader withTemplateModelDTO(NotificationTemplateModelDTO notificationTemplateModelDTO) {
        this.templateModelDTO = notificationTemplateModelDTO;
        Comment comment = notificationTemplateModelDTO.getComment();
        if (comment == null) {
            templateModelDTO.setInvoker(systemService.getSystem().getUser());
        } else {
            templateModelDTO.setInvoker(comment.getUser());
        }
        propertyLoader = applicationContext.getBean(PropertyLoader.class).withResource(notificationTemplateModelDTO.getResource());
        return this;
    }

    private String buildRedirectionControl(String linkUrl, PrismDisplayProperty linkLabel) throws IOException, TemplateException {
        return buildRedirectionControl(linkUrl, linkLabel, null, null);
    }

    private String buildRedirectionControl(String linkUrl, PrismDisplayProperty linkLabel, String declineLinkUrl, PrismDisplayProperty declineLinkLabel)
            throws IOException, TemplateException {
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", linkUrl, "label", propertyLoader.load(linkLabel));
        model.put("link", link);

        if (declineLinkUrl != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", declineLinkUrl, "label", propertyLoader.load(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        String emailControlTemplate = Resources.toString(Resources.getResource(emailControlTemplateLocation), Charsets.UTF_8);
        Template template = new Template("Email Control Template", emailControlTemplate, freemarkerConfig.getConfiguration());

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private String getConfirmedAssignedUser(PrismRole roleId) {
        Set<CommentAssignedUser> assignees = templateModelDTO.getComment().getAssignedUsers();
        for (CommentAssignedUser assignee : assignees) {
            if (assignee.getRole().getId() == roleId && assignee.getRoleTransitionType() == CREATE) {
                return assignee.getUser().getFullName();
            }
        }
        return null;
    }
}
