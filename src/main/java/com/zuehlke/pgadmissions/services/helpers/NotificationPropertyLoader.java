package com.zuehlke.pgadmissions.services.helpers;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_ACTIVATE_ACCOUNT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLICATION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_APPLY;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_COMMENT_CONTENT_NOT_PROVIDED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DATE_TIME_FORMAT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_DECLINE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HELPDESK;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HELPDESK_REPORT;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HOMEPAGE;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_INSTITUTION_PLURAL;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROCEED;
import static com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_PROJECT_PLURAL;
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
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.dto.NotificationDefinitionModelDTO;
import com.zuehlke.pgadmissions.exceptions.AbortMailSendException;
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

    private NotificationDefinitionModelDTO notificationDefinitionModelDTO;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${application.api.url}")
    private String applicationApiUrl;

    @Autowired
    private AdvertService advertService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private FreeMarkerConfig freemarkerConfig;

    public String load(PrismNotificationDefinitionProperty property) {
        String value = (String) ReflectionUtils.invokeMethod(this, ReflectionUtils.getMethodName(property));
        return value == null ? "[" + propertyLoader.load(SYSTEM_NOTIFICATION_TEMPLATE_PROPERTY_ERROR) + ". " + propertyLoader.load(SYSTEM_HELPDESK_REPORT)
                + ": " + notificationDefinitionModelDTO.getResource().getSystem().getHelpdesk() + "]" : value;
    }

    public String getTemplateUserFullName() {
        return notificationDefinitionModelDTO.getUser().getFullName();
    }

    public String getTemplateUserFirstName() {
        return notificationDefinitionModelDTO.getUser().getFirstName();
    }

    public String getTemplateUserLastName() {
        return notificationDefinitionModelDTO.getUser().getLastName();
    }

    public String getTemplateUserEmail() {
        return notificationDefinitionModelDTO.getUser().getEmail();
    }

    public String getTemplateUserActivationCode() {
        return notificationDefinitionModelDTO.getUser().getActivationCode();
    }

    public String getTemplateAuthorFullName() {
        return notificationDefinitionModelDTO.getAuthor().getFullName();
    }

    public String getTemplateAuthorEmail() {
        return notificationDefinitionModelDTO.getAuthor().getEmail();
    }

    public String getTemplateInvokerFullName() {
        return notificationDefinitionModelDTO.getInvoker().getFullName();
    }

    public String getTemplateInvokerEmail() {
        return notificationDefinitionModelDTO.getInvoker().getEmail();
    }

    public String getTemplateSystemTitle() {
        return notificationDefinitionModelDTO.getResource().getSystem().getTitle();
    }

    public String getTemplateSystemHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(applicationUrl, SYSTEM_HOMEPAGE);
    }

    public String getTemplateHelpdesk() throws IOException, TemplateException {
        return buildRedirectionControl(notificationDefinitionModelDTO.getResource().getHelpdeskDisplay(), SYSTEM_HELPDESK);
    }

    public String getActionComplete() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_PROCEED, notificationDefinitionModelDTO.getTransitionAction().isDeclinableAction() ? SYSTEM_DECLINE : null);
    }

    public String getActionViewEdit() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_VIEW_EDIT);
    }

    public String getCommentContent() {
        String content = notificationDefinitionModelDTO.getComment().getContent();
        return content == null ? propertyLoader.load(SYSTEM_COMMENT_CONTENT_NOT_PROVIDED) : "\"" + content + "\"";
    }

    public String getCommentDateTime() {
        return notificationDefinitionModelDTO.getComment().getCreatedTimestampDisplay(propertyLoader.load(SYSTEM_DATE_TIME_FORMAT));
    }

    public String getCommentTransitionOutcome() {
        String resourceName = notificationDefinitionModelDTO.getResource().getResourceScope().name();
        String outcomePostfix = Iterables.getLast(Lists.newArrayList(notificationDefinitionModelDTO.getComment().getTransitionState().getId().name().split("_")));
        return propertyLoader.load(PrismDisplayPropertyDefinition.valueOf(resourceName + "_COMMENT_" + outcomePostfix));
    }

    public String getApplicationCreatorFullName() {
        return notificationDefinitionModelDTO.getResource().getApplication().getUser().getFullName();
    }

    public String getApplicationCode() {
        return notificationDefinitionModelDTO.getResource().getApplication().getCode();
    }

    public String getApplicationProjectOrProgramTitle() {
        return notificationDefinitionModelDTO.getResource().getApplication().getProjectOrProgramTitleDisplay();
    }

    public String getApplicationProjectOrProgramCode() {
        return notificationDefinitionModelDTO.getResource().getApplication().getProjectOrProgramCodeDisplay();
    }

    public String getApplicationProgramType() {
        PrismProgramType programType = PrismProgramType.valueOf(notificationDefinitionModelDTO.getResource().getApplication().getProgram().getProgramType().getCode());
        return propertyLoader.load(programType.getDisplayProperty());
    }

    public String getApplicationInterviewDateTime() {
        return notificationDefinitionModelDTO.getComment().getInterviewDateTimeDisplay(propertyLoader.load(SYSTEM_DATE_TIME_FORMAT));
    }

    public String getApplicationInterviewTimeZone() {
        return notificationDefinitionModelDTO.getComment().getInterviewTimeZoneDisplay();
    }

    public String getApplicationInterviewerInstructions() {
        CommentApplicationInterviewInstruction interviewInstruction = notificationDefinitionModelDTO.getComment().getApplicationInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getInterviewerInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationIntervieweeInstructions() {
        CommentApplicationInterviewInstruction interviewInstruction = notificationDefinitionModelDTO.getComment().getApplicationInterviewInstruction();
        String instructions = interviewInstruction == null ? null : interviewInstruction.getIntervieweeInstructions();
        return instructions == null ? propertyLoader.load(SYSTEM_VALUE_NOT_PROVIDED) : instructions;
    }

    public String getApplicationInterviewLocation() throws IOException, TemplateException {
        CommentApplicationInterviewInstruction interviewInstruction = notificationDefinitionModelDTO.getComment().getApplicationInterviewInstruction();
        String interviewLocation = interviewInstruction == null ? null : interviewInstruction.getInterviewLocation();
        return interviewLocation == null ? "<p>" + propertyLoader.load(APPLICATION_COMMENT_DIRECTIONS_NOT_PROVIDED) + "</p>" : buildRedirectionControl(
                interviewLocation, APPLICATION_COMMENT_DIRECTIONS);
    }

    public String getApplicationConfirmedPositionTitle() {
        CommentApplicationPositionDetail positionDetail = notificationDefinitionModelDTO.getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionTitle();
    }

    public String getApplicationConfirmedPositionDescription() {
        CommentApplicationPositionDetail positionDetail = notificationDefinitionModelDTO.getComment().getPositionDetail();
        return positionDetail == null ? null : positionDetail.getPositionDescription();
    }

    public String getApplicationConfirmedStartDate() {
        return notificationDefinitionModelDTO.getComment().getPositionProvisionalStartDateDisplay(propertyLoader.load(SYSTEM_DATE_FORMAT));
    }

    public String getApplicationConfirmedPrimarySupervisor() {
        return getCommentAssigneesAsString(APPLICATION_PRIMARY_SUPERVISOR);
    }

    public String getApplicationConfirmedSecondarySupervisor() {
        return getCommentAssigneesAsString(APPLICATION_SECONDARY_SUPERVISOR);
    }

    public String getApplicationConfirmedOfferConditions() {
        CommentApplicationOfferDetail offerDetail = notificationDefinitionModelDTO.getComment().getOfferDetail();
        return offerDetail == null ? null : offerDetail.getAppointmentConditions();
    }

    public String getApplicationRejectionReason() {
        return notificationDefinitionModelDTO.getComment().getRejectionReasonDisplay();
    }

    public String getProjectTitle() {
        return notificationDefinitionModelDTO.getResource().getProject().getTitle();
    }

    public String getProjectCode() {
        return notificationDefinitionModelDTO.getResource().getProject().getCode();
    }

    public String getProgramTitle() {
        return notificationDefinitionModelDTO.getResource().getProgram().getTitle();
    }

    public String getProgramCode() {
        return notificationDefinitionModelDTO.getResource().getProgram().getCode();
    }

    public String getInstitutionTitle() {
        return notificationDefinitionModelDTO.getResource().getInstitution().getTitle();
    }

    public String getInstitutionCode() {
        return notificationDefinitionModelDTO.getResource().getInstitution().getCode();
    }

    public String getInstitutionHomepage() {
        return notificationDefinitionModelDTO.getResource().getInstitution().getHomepage();
    }

    public String getInstitutionDataImportError() {
        return notificationDefinitionModelDTO.getDataImportErrorMessage();
    }

    public String getSystemApplicationHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_APPLICATION_PLURAL);
    }

    public String getSystemApplicationRecommendation() throws IOException, TemplateException, AbortMailSendException {
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(notificationDefinitionModelDTO.getUser());

        if (!advertRecommendations.isEmpty()) {
            List<String> recommendations = Lists.newLinkedList();

            for (AdvertRecommendationDTO advertRecommendation : advertRecommendations) {
                Advert advert = advertRecommendation.getAdvert();

                Program program = advert.getProgram();
                Project project = advert.getProject();

                String title = "<b>" + advert.getTitle() + "</b>";
                String summary = advert.getSummary();

                String applyHomepage = advert.getApplyHomepage();
                applyHomepage = applyHomepage == null ? buildRedirectionUrl(project == null ? program : project, notificationDefinitionModelDTO.getTransitionAction(),
                        notificationDefinitionModelDTO.getUser()) : applyHomepage;

                recommendations.add(Joiner.on("<br/>").skipNulls().join(title, summary, buildRedirectionControl(applyHomepage, SYSTEM_APPLY)));
            }

            return "<p>" + Joiner.on("<p></p>").join(recommendations) + "</p>";
        }
        throw new AbortMailSendException("No recommended adverts found for user: " + notificationDefinitionModelDTO.getUser().getId().toString());
    }

    public String getSystemProjectHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_PROJECT_PLURAL);
    }

    public String getSystemProgramHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(PrismDisplayPropertyDefinition.SYSTEM_PROGRAM_PLURAL);
    }

    public String getSystemInstitutionHomepage() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_INSTITUTION_PLURAL);
    }

    public String getSystemUserNewPassword() throws IOException, TemplateException {
        return notificationDefinitionModelDTO.getNewPassword();
    }

    public String getSystemUserAccountManagement() throws IOException, TemplateException {
        String url = applicationApiUrl + "/mail/account";
        return buildRedirectionControl(url, SYSTEM_USER_ACCOUNT);
    }

    public String getSystemUserAccountActivation() throws IOException, TemplateException {
        return buildRedirectionControl(SYSTEM_ACTIVATE_ACCOUNT);
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

    private String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel) throws IOException, TemplateException {
        return buildRedirectionControl(linkLabel, null);
    }

    private String buildRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel) throws IOException, TemplateException {
        return buildRedirectionControl(url, linkLabel, null);
    }

    private String buildRedirectionControl(PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel) throws IOException,
            TemplateException {
        Resource resource = notificationDefinitionModelDTO.getResource();
        String url = buildRedirectionUrl(resource, notificationDefinitionModelDTO.getTransitionAction(), notificationDefinitionModelDTO.getUser());
        return buildRedirectionControl(url, linkLabel, declineLinkLabel);
    }

    private String buildRedirectionControl(String url, PrismDisplayPropertyDefinition linkLabel, PrismDisplayPropertyDefinition declineLinkLabel)
            throws IOException, TemplateException {
        Map<String, Object> model = Maps.newHashMap();
        ImmutableMap<String, String> link = ImmutableMap.of("url", url, "label", propertyLoader.load(linkLabel));
        model.put("link", link);

        if (declineLinkLabel != null) {
            ImmutableMap<String, String> declineLink = ImmutableMap.of("url", url + "&decline=1", "label", propertyLoader.load(declineLinkLabel));
            model.put("declineLink", declineLink);
        }

        String emailControlTemplate = Resources.toString(Resources.getResource("email/email_control_template.ftl"), Charsets.UTF_8);
        Template template = new Template("Email Control Template", emailControlTemplate, freemarkerConfig.getConfiguration());

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

    private String buildRedirectionUrl(Resource resource, PrismAction actionId, User user) {
        Resource operative = (Resource) ReflectionUtils.getProperty(resource, actionId.getScope().getLowerCamelName());
        return applicationApiUrl + "/mail/activate?resourceId=" + operative.getId() + "&actionId=" + actionId.name() + "&activationCode="
                + user.getActivationCode();
    }

    private String getCommentAssigneesAsString(PrismRole roleId) {
        Set<CommentAssignedUser> assigneeObjects = notificationDefinitionModelDTO.getComment().getAssignedUsers();
        Set<String> assigneeStrings = Sets.newTreeSet();
        for (CommentAssignedUser assigneeObject : assigneeObjects) {
            if (assigneeObject.getRole().getId() == roleId && assigneeObject.getRoleTransitionType() == CREATE) {
                assigneeStrings.add(assigneeObject.getUser().getFullName());
            }
        }
        return Joiner.on(", ").join(assigneeStrings);
    }

}
