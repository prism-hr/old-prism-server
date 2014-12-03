package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ACTIVATE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CONTENT_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_TIME_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DECLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HELPDESK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HELPDESK_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NEW_PASSWORD;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROCEED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROGRAM_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT_LIST;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_USER_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VIEW_EDIT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_PRIMARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole.APPLICATION_SECONDARY_SUPERVISOR;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRoleTransitionType.CREATE;

import java.io.IOException;
import java.util.List;
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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationInterviewInstruction;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationOfferDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentApplicationPositionDetail;
import com.zuehlke.pgadmissions.domain.comment.CommentAssignedUser;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismNotificationDefinitionProperty;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismRole;
import com.zuehlke.pgadmissions.domain.program.Program;
import com.zuehlke.pgadmissions.domain.project.Project;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.SystemService;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotificationPropertyLoader {

    private PropertyLoader propertyLoader;

    private NotificationDefinitionModelDTO templateModelDTO;

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

    public String load(PrismNotificationDefinitionProperty property) {
        String value = (String) ReflectionUtils.invokeMethod(this, ReflectionUtils.getMethodName(property));
        return value == null ? "[" + propertyLoader.load(SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR) + ". " + propertyLoader.load(SYSTEM_HELPDESK_REPORT)
                + ": " + templateModelDTO.getResource().getSystem().getHelpdesk() + "]" : value;
    }

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
        return buildRedirectionControl(SYSTEM_HELPDESK);
    }

    public String getTemplateViewEdit() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_VIEW_EDIT);
    }

    public String getActionComplete() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_PROCEED, templateModelDTO.getTransitionAction().isDeclinableAction() ? SYSTEM_DECLINE : null);
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
        String outcomePostfix = Iterables.getLast(Lists.newArrayList(templateModelDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.load(PrismDisplayPropertyDefinition.valueOf(resourceName + "_COMMENT_" + outcomePostfix));
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
        CommentApplicationInterviewInstruction interviewInstruction = templateModelDTO.getComment().getInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getInterviewerInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationIntervieweeInstructions() {
        CommentApplicationInterviewInstruction interviewInstruction = templateModelDTO.getComment().getInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getIntervieweeInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationInterviewLocation() throws IOException, TemplateException {
        CommentApplicationInterviewInstruction interviewInstruction = templateModelDTO.getComment().getInterviewInstruction();
        String interviewLocation = interviewInstruction == null ? null : interviewInstruction.getInterviewLocation();
        return interviewLocation == null ? "<p>" + propertyLoader.load(APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED) + "</p>" : buildRedirectionControl(
                interviewLocation, APPLICATION_COMMENT_DIRECTIONS);
    }

    public String getApplicationConfirmedPositionTitle() {
        CommentApplicationPositionDetail positionDetail = templateModelDTO.getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionTitle();
    }

    public String getApplicationConfirmedPositionDescription() {
        CommentApplicationPositionDetail positionDetail = templateModelDTO.getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionDescription();
    }

    public String getApplicationConfirmedStartDate() {
        return templateModelDTO.getComment().getPositionProvisionalStartDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT));
    }

    public String getApplicationConfirmedPrimarySupervisor() {
        return getCommentAssigneesAsString(APPLICATION_PRIMARY_SUPERVISOR);
    }

    public String getApplicationConfirmedSecondarySupervisor() {
        return getCommentAssigneesAsString(APPLICATION_SECONDARY_SUPERVISOR);
    }

    public String getApplicationConfirmedOfferConditions() {
        CommentApplicationOfferDetail offerDetail = templateModelDTO.getComment().getOfferDetail();
        return offerDetail == null ? null : offerDetail.getAppointmentConditions();
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
        return buildRedirectionControl(SYSTEM_APPLICATION_LIST);
    }

    public String getSystemApplicationRecommendation() throws IOException, TemplateException {
        List<Advert> adverts = advertService.getRecommendedAdverts(templateModelDTO.getUser());
        List<String> recommendations = Lists.newLinkedList();

        for (Advert advert : adverts) {
            Program program = advert.getProgram();
            Project project = advert.getProject();
            Resource resource = project == null ? program : project;

            String applyUri = advert.getApplyHomepage();
            applyUri = applyUri == null ? buildRedirectionUri(resource, templateModelDTO.getTransitionAction(), templateModelDTO.getUser()) : applyUri;

            recommendations.add("<p>" + program.getTitle() + project == null ? "<br/>" : "<br/>" + project == null ? "" : project.getTitle() + "<br/>"
                    + buildRedirectionControl(applyUri, SYSTEM_APPLY) + "</p>");
        }

        return Joiner.on("").join(recommendations);
    }

    public String getSystemProjectHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_PROJECT_LIST);
    }

    public String getSystemProgramHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_PROGRAM_LIST);
    }

    public String getSystemInstitutionHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_INSTITUTION_LIST);
    }

    public String getSystemUserNewPassword() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_NEW_PASSWORD);
    }

    public String getSystemUserAccountManagement() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_USER_ACCOUNT);
    }

    public String getSystemUserAccountActivation() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_ACTIVATE_ACCOUNT);
    }

    public NotificationPropertyLoader localize(NotificationDefinitionModelDTO templateModelDTO, PropertyLoader propertyLoader) {
        this.templateModelDTO = templateModelDTO;
        Comment comment = this.templateModelDTO.getComment();
        if (comment == null) {
            this.templateModelDTO.setInvoker(systemService.getSystem().getUser());
        } else {
            this.templateModelDTO.setInvoker(comment.getUser());
        }
        this.propertyLoader = propertyLoader;
        return this;
    }

    private String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel) throws IOException, TemplateException {
        return buildRedirectionControl(linkLabel, null);
    }

    private String buildRedirectionControl(String uri, PrismDisplayPropertyDefinition linkLabel) throws IOException, TemplateException {
        return buildRedirectionControl(uri, linkLabel, null);
    }

    private String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel) throws IOException,
            TemplateException {
        Resource resource = templateModelDTO.getResource();
        String uri = buildRedirectionUri(resource, templateModelDTO.getTransitionAction(), templateModelDTO.getUser());
        return buildRedirectionControl(uri, linkLabel, declineLinkLabel);
    }

    private String buildRedirectionControl(String uri, PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel)
            throws IOException, TemplateException {
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", uri, "label", propertyLoader.load(linkLabel));
        model.put("link", link);

        if (declineLinkLabel != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", uri + "&decline=1", "label", propertyLoader.load(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        String emailControlTemplate = Resources.toString(Resources.getResource(emailControlTemplateLocation), Charsets.UTF_8);
        Template template = new Template("Email Control Template", emailControlTemplate, freemarkerConfig.getConfiguration());

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private String buildRedirectionUri(Resource resource, PrismAction actionId, User user) {
        Resource operative = (Resource) ReflectionUtils.getProperty(resource, actionId.getScope().getLowerCaseName());
        return operative.getSystem().getHomepage() + "/#/activate?resourceId=" + operative.getId() + "&actionId=" + actionId.name() + "&activationCode="
                + user.getActivationCode();
    }

    private String getCommentAssigneesAsString(PrismRole roleId) {
        Set<CommentAssignedUser> assigneeObjects = templateModelDTO.getComment().getAssignedUsers();
        Set<String> assigneeStrings = Sets.newTreeSet();
        for (CommentAssignedUser assigneeObject : assigneeObjects) {
            if (assigneeObject.getRole().getId() == roleId && assigneeObject.getRoleTransitionType() == CREATE) {
                assigneeStrings.add(assigneeObject.getUser().getFullName());
            }
        }
        return Joiner.on(", ").join(assigneeStrings);
    }

}
