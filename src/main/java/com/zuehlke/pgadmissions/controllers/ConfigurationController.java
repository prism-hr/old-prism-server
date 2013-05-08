package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.EmailTemplateName.valueOf;
import static java.util.Arrays.sort;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.EmailTemplate;
import com.zuehlke.pgadmissions.domain.Person;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.ReminderInterval;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.StageDuration;
import com.zuehlke.pgadmissions.domain.Throttle;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.EmailTemplateName;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.RegistryUserDTO;
import com.zuehlke.pgadmissions.dto.StageDurationDTO;
import com.zuehlke.pgadmissions.exceptions.EmailTemplateException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.propertyeditors.PersonPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.StageDurationPropertyEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EmailTemplateService;
import com.zuehlke.pgadmissions.services.PorticoQueueService;
import com.zuehlke.pgadmissions.services.ProgramsService;
import com.zuehlke.pgadmissions.services.ThrottleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.validators.FeedbackCommentValidator;

@Controller
@RequestMapping("/configuration")
public class ConfigurationController {

	private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";
	private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";

	private final StageDurationPropertyEditor stageDurationPropertyEditor;

	private final PersonPropertyEditor registryPropertyEditor;

	private final UserService userService;

	private final ConfigurationService configurationService;

	private final EmailTemplateService templateService;

	private final ThrottleService throttleService;

	private final PorticoQueueService queueService;

	private final ProgramsService programsService;

	private final ScoringDefinitionParser scoringDefinitionParser;

	private final ScoreFactory scoreFactory;

	private final ScoresPropertyEditor scoresPropertyEditor;

	private final FeedbackCommentValidator dummyCommentValidator;

	public ConfigurationController() {
		this(null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@Autowired
	public ConfigurationController(StageDurationPropertyEditor stageDurationPropertyEditor, PersonPropertyEditor registryPropertyEditor,
	                UserService userService, ConfigurationService configurationService, EmailTemplateService templateService, ThrottleService throttleService,
	                PorticoQueueService queueService, ProgramsService programsService, ScoringDefinitionParser scoringDefinitionParser,
	                ScoreFactory scoreFactory, ScoresPropertyEditor scoresPropertyEditor, FeedbackCommentValidator dummyCommentValidator) {
		this.stageDurationPropertyEditor = stageDurationPropertyEditor;
		this.registryPropertyEditor = registryPropertyEditor;
		this.userService = userService;
		this.configurationService = configurationService;
		this.templateService = templateService;
		this.throttleService = throttleService;
		this.queueService = queueService;
		this.programsService = programsService;
		this.scoringDefinitionParser = scoringDefinitionParser;
		this.scoreFactory = scoreFactory;
		this.scoresPropertyEditor = scoresPropertyEditor;
		this.dummyCommentValidator = dummyCommentValidator;
	}

	@InitBinder(value = "stageDurationDTO")
	public void registerValidatorsAndPropertyEditors(WebDataBinder binder) {
		binder.registerCustomEditor(StageDuration.class, stageDurationPropertyEditor);
	}

	@InitBinder(value = "registryUserDTO")
	public void registerValidatorsAndPropertyEditorsForRegistryUsers(WebDataBinder binder) {
		binder.registerCustomEditor(Person.class, registryPropertyEditor);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String getConfigurationPage() {
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR) && !getUser().isInRole(Authority.ADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		return CONFIGURATION_VIEW_NAME;
	}

	@RequestMapping(method = RequestMethod.GET, value = "config_section")
	public String getConfigurationSection() {
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return "/private/common/simpleMessage";
		}
		return CONFIGURATION_SECTION_NAME;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String submit(@ModelAttribute StageDurationDTO stageDurationDto, @ModelAttribute RegistryUserDTO registryUserDTO,
	                @ModelAttribute ReminderInterval reminderInterval) {
		if (!getUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			throw new ResourceNotFoundException();
		}
		configurationService.saveConfigurations(stageDurationDto.getStagesDuration(), registryUserDTO.getRegistryUsers(), reminderInterval);
		return "redirect:/configuration/config_section";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{id:\\d+}")
	@ResponseBody
	public Map<String, Object> getTemplateVersion(@PathVariable Long id) {
		EmailTemplate template = templateService.getEmailTemplate(id);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("content", template.getContent());
		result.put("version", template.getVersion());
		result.put("subject", template.getSubject());
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getThrottle")
	@ResponseBody
	public Map<String, Object> getThrottle() {
		Throttle throttle = throttleService.getThrottle();
		if (throttle == null) {
			return Collections.emptyMap();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("throttleId", throttle.getId());
		result.put("enabled", throttle.getEnabled());
		result.put("batchSize", throttle.getBatchSize());
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/updateThrottle")
	@ResponseBody
	public Map<String, String> updateThrottle(@RequestParam Integer id, @RequestParam Boolean enabled, @RequestParam String batchSize) {
		boolean hasSwitchedFromFalseToTrue = throttleService.userTurnedOnThrottle(enabled);

		try {
			throttleService.updateThrottleWithNewValues(enabled, batchSize);
		} catch (NumberFormatException e) {
			return Collections.singletonMap("error", "The throttling batch size must be a valid positive number");
		}

		if (hasSwitchedFromFalseToTrue) {
			queueService.sendQueuedApprovedApplicationsToPortico();
		}

		return Collections.emptyMap();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/editScoringDefinition")
	@ResponseBody
	public Map<String, String> editScoringDefinition(@RequestParam String programCode, @RequestParam ScoringStage scoringStage,
	                @RequestParam String scoringContent, HttpServletResponse response) {
		Map<String, String> errors = validateScoringDefinition(programCode, scoringContent);
		if (errors.isEmpty()) {
			programsService.applyScoringDefinition(programCode, scoringStage, scoringContent);
		}
		return errors;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/getScoringDefinition")
	@ResponseBody
	public String getScoringDefinition(@RequestParam String programCode, @RequestParam ScoringStage scoringStage) {
		Program program = programsService.getProgramByCode(programCode);
		ScoringDefinition scoringDefinition = program.getScoringDefinitions().get(scoringStage);
		if (scoringDefinition != null) {
			return scoringDefinition.getContent();
		} else {
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{templateName:[a-zA-Z_]+}")
	@ResponseBody
	public Map<Object, Object> getVersionsForTemplate(@PathVariable String templateName) {
		EmailTemplate template = templateService.getActiveEmailTemplate(valueOf(templateName));
		Map<Long, String> versions = templateService.getEmailTemplateVersions(template.getName());
		Map<Object, Object> result = new HashMap<Object, Object>();
		result.put("content", template.getContent());
		result.put("versions", versions);
		result.put("subject", template.getSubject());
		result.put("activeVersion", template.getId());
		result.putAll(versions);
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = { "saveEmailTemplate/{templateName:[a-zA-Z_]+}" })
	@ResponseBody
	public Map<String, Object> saveTemplate(@PathVariable EmailTemplateName templateName, @RequestParam String content, @RequestParam String subject) {
		return saveNewTemplate(templateName, content, subject);
	}

	private Map<String, Object> saveNewTemplate(EmailTemplateName templateName, String content, String subject) {
		EmailTemplate template = templateService.saveNewEmailTemplate(templateName, content, subject);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("id", template.getId());
		result.put("version", new SimpleDateFormat("yyyy/M/d - HH:mm:ss").format(template.getVersion()));
		return result;
	}

	@RequestMapping(method = RequestMethod.POST, value = { "activateEmailTemplate/{templateName:[a-zA-Z_]+}/{id:\\d+}" })
	@ResponseBody
	public Map<String, Object> activateTemplate(@PathVariable String templateName, @PathVariable Long id, @RequestParam Boolean saveCopy,
	                @RequestParam(required = false) String newContent, @RequestParam(required = false) String newSubject) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (saveCopy != null && saveCopy) {
			result = saveNewTemplate(valueOf(templateName), newContent, newSubject);
			id = (Long) result.get("id");
		}
		if (result.containsKey("error")) {
			return result;
		}
		Long previousId = templateService.getActiveEmailTemplate(valueOf(templateName)).getId();

		try {
			templateService.activateEmailTemplate(valueOf(templateName), id);
			result.put("previousTemplateId", (Object) previousId);
			return result;
		} catch (EmailTemplateException ete) {
			return Collections.singletonMap("error", (Object) ete.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = { "deleteEmailTemplate/{id:\\d+}" })
	@ResponseBody
	public Map<String, Object> deleteTemplate(@PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		EmailTemplate toDeleteTemplate = templateService.getEmailTemplate(id);
		EmailTemplateName templateName = toDeleteTemplate.getName();
		EmailTemplate activeTemplate = templateService.getActiveEmailTemplate(templateName);
		result.put("activeTemplateId", activeTemplate.getId());
		result.put("activeTemplateContent", activeTemplate.getContent());
		result.put("activeTemplateSubject", activeTemplate.getSubject());
		try {
			templateService.deleteTemplateVersion(toDeleteTemplate);
		} catch (EmailTemplateException ete) {
			result.put("error", ete.getMessage());
		}
		return result;
	}

	// Not used now, may be in the future
	@RequestMapping(method = RequestMethod.POST, value = { "previewTemplate" })
	@ResponseBody
	public Map<String, String> getTemplatePreview(@RequestParam String templateContent) {
		return Collections.singletonMap("template", templateService.processTemplateContent(templateContent));
	}

	@ModelAttribute("templateTypes")
	public EmailTemplateName[] getTemplateTypes() {
		EmailTemplateName[] names = EmailTemplateName.values();
		sort(names, new Comparator<EmailTemplateName>() {
			@Override
			public int compare(EmailTemplateName o1, EmailTemplateName o2) {
				return o1.displayValue().compareTo(o2.displayValue());
			}
		});
		return names;
	}

	@ModelAttribute("stages")
	public ApplicationFormStatus[] getConfigurableStages() {
		return configurationService.getConfigurableStages();
	}

	@ModelAttribute("user")
	public RegisteredUser getUser() {
		return userService.getCurrentUser();
	}

	@ModelAttribute("stageDurations")
	public Map<String, StageDuration> getStageDurations() {
		Map<ApplicationFormStatus, StageDuration> stageDurations = configurationService.getStageDurations();
		Map<String, StageDuration> durations = new HashMap<String, StageDuration>();
		for (ApplicationFormStatus status : stageDurations.keySet()) {
			durations.put(status.toString(), stageDurations.get(status));
		}
		return durations;
	}

	@ModelAttribute("reminderInterval")
	public ReminderInterval getReminderInterval() {
		return configurationService.getReminderInterval();
	}

	@ModelAttribute("allRegistryUsers")
	public List<Person> getAllRegistryContacts() {
		return configurationService.getAllRegistryUsers();
	}

	@ModelAttribute("units")
	public DurationUnitEnum[] getUnits() {
		return DurationUnitEnum.values();
	}

	@ModelAttribute("scoringStages")
	public ScoringStage[] getScoringStages() {
		return ScoringStage.values();
	}

	@ModelAttribute("programs")
	public List<Program> getPrograms() {
		if (userService.getCurrentUser().isInRole(Authority.SUPERADMINISTRATOR)) {
			return programsService.getAllPrograms();
		}
		return userService.getCurrentUser().getProgramsOfWhichAdministrator();
	}

	private Map<String, String> validateScoringDefinition(String programCode, String scoringContent) {
		Program program = programsService.getProgramByCode(programCode);
		if (program == null) {
			return Collections.singletonMap("programCode", "Given program code is not valid");
		}

		if (scoringContent != "") {
			try {
				scoringDefinitionParser.parseScoringDefinition(scoringContent);
			} catch (ScoringDefinitionParseException e) {
				return Collections.singletonMap("scoringContent", e.getLocalizedMessage());
			}
		}
		return Collections.emptyMap();
	}

	@InitBinder(value = "dummyComment")
	public void registerBinders(WebDataBinder binder) {
		binder.setValidator(dummyCommentValidator);
		binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/fakeSubmitScores")
	public String dummySubmitScores(@ModelAttribute("dummyComment") Comment dummyComment, @RequestParam String scoringContent, BindingResult result, Model model)
	                throws ScoringDefinitionParseException {
		List<Score> scores = dummyComment.getScores();
		CustomQuestions parseScoringDefinition = scoringDefinitionParser.parseScoringDefinition(scoringContent);
		model.addAttribute("scores", scores);
		model.addAttribute("alertForScoringQuestions", parseScoringDefinition.getAlert());
		if (scores != null) {
			List<Question> questions = parseScoringDefinition.getQuestion();
			for (int i = 0; i < scores.size(); i++) {
				Score score = scores.get(i);
				score.setOriginalQuestion(questions.get(i));
			}
		}
		dummyCommentValidator.validate(dummyComment, result);
		model.addAttribute("errorsContainerName", "dummyComment");
		return "/private/staff/scores";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/previewScoringDefinition")
	public String previewScoringDefinition(@RequestParam String programCode, @RequestParam String scoringContent, Model model, HttpServletResponse response)
	                throws IOException {
		String errorMessage = "";
		try {
			CustomQuestions parseScoringDefinition = scoringDefinitionParser.parseScoringDefinition(scoringContent);
			List<Score> scores = scoreFactory.createScores(parseScoringDefinition.getQuestion());
			model.addAttribute("scores", scores);
			model.addAttribute("alertForScoringQuestions", parseScoringDefinition.getAlert());
			return "/private/staff/scores";
		} catch (ScoringDefinitionParseException e) {
			errorMessage = e.getLocalizedMessage();
		}

		response.sendError(418, errorMessage);
		return null;
	}
}
