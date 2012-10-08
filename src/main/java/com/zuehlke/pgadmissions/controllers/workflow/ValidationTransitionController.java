package com.zuehlke.pgadmissions.controllers.workflow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
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
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Badge;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ValidationComment;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.BadgeService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.CommentFactory;
import com.zuehlke.pgadmissions.utils.DateUtils;
import com.zuehlke.pgadmissions.utils.StateTransitionViewResolver;
import com.zuehlke.pgadmissions.validators.StateChangeValidator;

@Controller
@RequestMapping("/progress")
public class ValidationTransitionController extends StateTransitionController {

	private final BadgeService badgeService;
	
	private final MessageSource messageSource;
	
	ValidationTransitionController() {
		 this(null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ValidationTransitionController(ApplicationsService applicationsService, UserService userService, CommentService commentService,
			CommentFactory commentFactory, StateTransitionViewResolver stateTransitionViewResolver, EncryptionHelper encryptionHelper,
			DocumentService documentService, ApprovalService approvalService, StateChangeValidator stateChangeValidator, 
			DocumentPropertyEditor documentPropertyEditor, BadgeService badgeService, MessageSource messageSource) {
		super(applicationsService, userService, commentService, commentFactory, stateTransitionViewResolver, encryptionHelper,documentService, approvalService, stateChangeValidator, documentPropertyEditor);
		this.messageSource = messageSource;
		this.badgeService = badgeService;
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/getPage")
	public String getStateTransitionView(@ModelAttribute ApplicationForm applicationForm) {
		return stateTransitionViewResolver.resolveView(applicationForm);
	}
	
	@ModelAttribute("comment")
	public ValidationComment getComment(@RequestParam String applicationId) {
		ValidationComment validationComment = new ValidationComment();
		validationComment.setApplication(getApplicationForm(applicationId));
		validationComment.setUser(getCurrentUser());
		return validationComment;
	}
	
	@RequestMapping(value="/submitValidationComment", method = RequestMethod.POST)
	public String addComment(@RequestParam String applicationId, @RequestParam String closingDate, @RequestParam String projectTitle, 
	        @Valid @ModelAttribute("comment") ValidationComment validationComment, BindingResult result, ModelMap modelMap) {
		try {
		    ApplicationForm form = getApplicationForm(applicationId);
		    Program programme = form.getProgram();
		    Date newClosingDate = null;
		    
		    if (StringUtils.isNotBlank(closingDate)) {
		        newClosingDate = new SimpleDateFormat("dd MMM yyyy").parse(closingDate);
                modelMap.put("closingDate", newClosingDate);
                
                boolean foundMatch = false;
                List<Date> existingClosingDates = badgeService.getAllClosingDatesByProgram(programme);
                for (Date existingClosingDate : existingClosingDates) {
                    if (org.apache.commons.lang.time.DateUtils.isSameDay(existingClosingDate, newClosingDate)) {
                        foundMatch = true;
                        break;
                    }
                }
                
		        if (!foundMatch && !DateUtils.isToday(newClosingDate) && newClosingDate.before(new Date())) {
		            Date oneMonthAgo = org.apache.commons.lang.time.DateUtils.addMonths(Calendar.getInstance().getTime(), -1);
		            if (!org.apache.commons.lang.time.DateUtils.isSameDay(newClosingDate, oneMonthAgo) && newClosingDate.before(oneMonthAgo)) {
		                modelMap.put("closingDate_error", messageSource.getMessage("date.field.notfuture", null, null));
		            }
		        }
		        
		        if (!modelMap.containsKey("closingDate_error")) {
		            form.setBatchDeadline(newClosingDate);
		        }
		    }
		    
		    if (!StringUtils.isBlank(projectTitle)) {
		        modelMap.put("projectTitle", projectTitle);
		        if (ESAPI.validator().isValidInput("input", projectTitle, "ExtendedAscii", 500, false)) {
	                form.setProjectTitle(projectTitle);
	            } else {	                
	                modelMap.put("projectTitle_error", messageSource.getMessage("text.field.nonextendedascii", null, null));
	            }
		    }
		    
		    if (modelMap.containsKey("closingDate_error") || modelMap.containsKey("projectTitle_error") || result.hasErrors()) {
		        return STATE_TRANSITION_VIEW;
		    }
		    
		    if (StringUtils.isNotBlank(closingDate) || StringUtils.isNotBlank(projectTitle)) {
		        Badge newBadge = new Badge();
		        newBadge.setClosingDate(newClosingDate);
		        newBadge.setProjectTitle(projectTitle);
		        newBadge.setProgram(programme);
		        badgeService.save(newBadge);
		    	applicationsService.save(form);
		    }
		    
		    commentService.save(validationComment);
		} catch (Exception e) {
		    return STATE_TRANSITION_VIEW;
		}

		return stateTransitionViewResolver.resolveView(getApplicationForm(applicationId));
	}
	
	@RequestMapping(value = "/getProjectTitles", method = RequestMethod.GET)
	@ResponseBody
	public String getProjectTitlesJson(@RequestParam String applicationId, @RequestParam String term) {
	    List<String> projectTitles = badgeService.getAllProjectTitlesByProgramFilteredByNameLikeCaseInsensitive(getApplicationForm(applicationId).getProgram(), term);
        ApplicationForm form = getApplicationForm(applicationId);
        if (!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
            projectTitles.add(form.getProjectTitle());
        } else if(!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() == null) {
            projectTitles.add(form.getProjectTitle());
        }
        Gson gson = new Gson();
        return gson.toJson(projectTitles);
	}
	
    @RequestMapping(value = "/getClosingDates", method = RequestMethod.GET)
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
            } catch (Throwable e) {
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
		if(form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
			allClosingDates.add(form.getBatchDeadline());
		}
		return allClosingDates;
	}
	
	@ModelAttribute("badgesByTitle")
	public List<String> getProjectTitles(@RequestParam String applicationId) {
		List<String> projectTitles = new ArrayList<String>();
		projectTitles = badgeService.getAllProjectTitlesByProgram(getApplicationForm(applicationId).getProgram());
		ApplicationForm form = getApplicationForm(applicationId);
		if(!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() != null && form.getBatchDeadline().before(new Date())) {
			projectTitles.add(form.getProjectTitle());
		}
		else if(!StringUtils.isBlank(form.getProjectTitle()) && form.getBatchDeadline() == null) {
			projectTitles.add(form.getProjectTitle());
		}
		return projectTitles;
	}
	
}
