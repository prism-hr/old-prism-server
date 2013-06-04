package com.zuehlke.pgadmissions.controllers.workflow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormUpdate;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationFormAccessService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.StateTransitionService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class ValidationTransitionController extends StateTransitionController {

    private static final String ERROR_CLOSING_DATE_FORMAT = "dd-MMM-yyyy";

    private static final String PROVIDED_CLOSING_DATE_FORMAT = "dd MMM yyyy";

    private final BadgeService badgeService;

    private final MessageSource messageSource;

    public ValidationTransitionController() {
        this(null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Autowired
    public ValidationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
            CommentFactory commentFactory, EncryptionHelper encryptionHelper, DocumentService documentService, ApprovalService approvalService,
            StateChangeValidator stateChangeValidator, DocumentPropertyEditor documentPropertyEditor, BadgeService badgeService, MessageSource messageSource,
            StateTransitionService stateTransitionService, ApplicationFormAccessService accessService, ActionsProvider actionsProvider) {
        super(applicationsService, userService, commentService, commentFactory, encryptionHelper, documentService, approvalService, stateChangeValidator,
                documentPropertyEditor, stateTransitionService, accessService, actionsProvider);
        this.messageSource = messageSource;
        this.badgeService = badgeService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getPage")
    public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm) {
        return stateTransitionService.resolveView(applicationForm);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/submitValidationComment")
    public String defaultGet() {
        return "redirect:/applications";
    }

    @ModelAttribute("comment")
    public ValidationComment getComment(@RequestParam String applicationId) {
        ValidationComment validationComment = new ValidationComment();
        validationComment.setApplication(getApplicationForm(applicationId));
        validationComment.setUser(getCurrentUser());
        return validationComment;
    }

    @RequestMapping(value = "/submitValidationComment", method = RequestMethod.POST)
    public String addComment(@RequestParam String applicationId, @RequestParam String closingDate, @RequestParam String projectTitle,
            @Valid @ModelAttribute("comment") ValidationComment comment, BindingResult result, ModelMap model,
            @RequestParam(required = false) Boolean delegate, @ModelAttribute("delegatedInterviewer") RegisteredUser delegatedInterviewer,
            @RequestParam(required = false) Boolean fastTrackApplication) {

        model.put("delegate", delegate);
        ApplicationForm form = getApplicationForm(applicationId);
        try {
            Program programme = form.getProgram();
            Date newClosingDate = null;
            Date today = new Date();

            if (StringUtils.isNotBlank(closingDate)) {
                newClosingDate = new SimpleDateFormat(PROVIDED_CLOSING_DATE_FORMAT).parse(closingDate);
                model.put("closingDate", newClosingDate);

                boolean foundMatch = false;
                List<Date> existingClosingDates = badgeService.getAllClosingDatesByProgram(programme);
                for (Date existingClosingDate : existingClosingDates) {
                    if (DateUtils.isSameDay(existingClosingDate, newClosingDate)) {
                        foundMatch = true;
                        break;
                    }
                }

                if (!foundMatch && isNotSameDay(newClosingDate, today) && newClosingDate.before(today)) {
                    Date oneMonthAgo = new DateTime().minusMonths(1).toDate();
                    if (isNotSameDay(newClosingDate, oneMonthAgo) && newClosingDate.before(oneMonthAgo)) {
                        SimpleDateFormat format = new SimpleDateFormat(ERROR_CLOSING_DATE_FORMAT);
                        model.put("closingDate_error", messageSource.getMessage("date.field.notbefore", new Object[] { format.format(oneMonthAgo) }, null));
                    }
                }

                if (!model.containsKey("closingDate_error")) {
                    form.setBatchDeadline(newClosingDate);
                }
            }

            if (!StringUtils.isBlank(projectTitle)) {
                model.put("projectTitle", projectTitle);
                if (ESAPI.validator().isValidInput("input", projectTitle, "ExtendedAscii", 500, false)) {
                    form.setProjectTitle(projectTitle);
                } else {
                    model.put("projectTitle_error", messageSource.getMessage("text.field.nonextendedascii", null, null));
                }
            }

            if (fastTrackApplication == null || model.containsKey("closingDate_error") || model.containsKey("projectTitle_error") || result.hasErrors()) {
                return STATE_TRANSITION_VIEW;
            }
            
            if (BooleanUtils.isNotTrue(delegate)) {
                form.setApplicationAdministrator(null);
            }

            if (BooleanUtils.isTrue(fastTrackApplication)) {
                applicationsService.fastTrackApplication(form.getApplicationNumber());
            }

            if (StringUtils.isNotBlank(closingDate) || StringUtils.isNotBlank(projectTitle)) {
                Badge badge = new Badge();
                badge.setClosingDate(newClosingDate);
                badge.setProjectTitle(projectTitle);
                badge.setProgram(programme);
                badgeService.save(badge);
                applicationsService.save(form);
            }

            form.addApplicationUpdate(new ApplicationFormUpdate(form, ApplicationUpdateScope.INTERNAL, new Date()));
            accessService.updateAccessTimestamp(form, getCurrentUser(), new Date());
            applicationsService.save(form);
            comment.setDate(new Date());
            commentService.save(comment);

            if (comment.getNextStatus() == ApplicationFormStatus.APPROVAL) {
                applicationsService.makeApplicationNotEditable(form);
            }

            if (answeredOneOfTheQuestionsUnsure(comment) && comment.getNextStatus() != ApplicationFormStatus.REJECTED) {
                form.setAdminRequestedRegistry(getCurrentUser());
                form.setRegistryUsersDueNotification(true);
                applicationsService.save(form);
            }

        } catch (Exception e) {
            return STATE_TRANSITION_VIEW;
        }

        if (BooleanUtils.isTrue(delegate)) {
            return "redirect:/applications?messageCode=delegate.success&application=" + form.getApplicationNumber();
        }

        applicationsService.refresh(form);
        return stateTransitionService.resolveView(form);
    }

    private boolean isNotSameDay(final Date date1, final Date date2) {
        return !DateUtils.isSameDay(date1, date2);
    }

    private boolean answeredOneOfTheQuestionsUnsure(final ValidationComment comment) {
        return comment.getHomeOrOverseas() == HomeOrOverseas.UNSURE || comment.getQualifiedForPhd() == ValidationQuestionOptions.UNSURE
                || comment.getEnglishCompentencyOk() == ValidationQuestionOptions.UNSURE;
    }

    @RequestMapping(value = "/getProjectTitles", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getProjectTitlesJson(@RequestParam String applicationId, @RequestParam String term) {
        List<String> projectTitles = badgeService.getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(getApplicationForm(applicationId).getProgram(),
                term);
        ApplicationForm form = getApplicationForm(applicationId);
        if (!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
            projectTitles.add(form.getProjectTitle());
        } else if (!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() == null) {
            projectTitles.add(form.getProjectTitle());
        }
        Gson gson = new Gson();
        return gson.toJson(projectTitles);
    }

    @RequestMapping(value = "/getClosingDates", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getClosingDatesJson(@RequestParam String applicationId) {
        List<Date> allClosingDates = new ArrayList<Date>();
        allClosingDates = badgeService.getAllClosingDatesByProgram(getApplicationForm(applicationId).getProgram());
        ApplicationForm form = getApplicationForm(applicationId);
        if (form.getBatchDeadline() != null) {
            allClosingDates.add(form.getBatchDeadline());
        }

        List<String> convertedDates = new ArrayList<String>();
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy");
        for (Date date : allClosingDates) {
            try {
                convertedDates.add(format.format(date));
            } catch (Exception e) {
                // do nothing
            }
        }
        Gson gson = new Gson();
        return gson.toJson(convertedDates);
    }

    @ModelAttribute("validationQuestionOptions")
    public ValidationQuestionOptions[] getValidationQuestionOptions() {
        return ValidationQuestionOptions.values();
    }

    @ModelAttribute("homeOrOverseasOptions")
    public HomeOrOverseas[] getHomeOrOverseasOptions() {
        return HomeOrOverseas.values();
    }

    @ModelAttribute("badgesByClosingDate")
    public List<Date> getClosingDates(@RequestParam String applicationId) {
        List<Date> allClosingDates = new ArrayList<Date>();
        allClosingDates = badgeService.getAllClosingDatesByProgram(getApplicationForm(applicationId).getProgram());
        ApplicationForm form = getApplicationForm(applicationId);
        if (form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
            allClosingDates.add(form.getBatchDeadline());
        }
        return allClosingDates;
    }

    @ModelAttribute("badgesByTitle")
    public List<String> getProjectTitles(@RequestParam String applicationId) {
        List<String> projectTitles = new ArrayList<String>();
        projectTitles = badgeService.getAllProjectTitlesByProgram(getApplicationForm(applicationId).getProgram());
        ApplicationForm form = getApplicationForm(applicationId);
        if (!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
            projectTitles.add(form.getProjectTitle());
        } else if (!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() == null) {
            projectTitles.add(form.getProjectTitle());
        }
        return projectTitles;
    }
}
