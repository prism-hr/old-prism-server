package com.zuehlke.pgadmissions.controllers;

import static com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate.valueOf;
import static java.util.Arrays.sort;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.CommentCustomQuestion;
import com.zuehlke.pgadmissions.domain.NotificationTemplate;
import com.zuehlke.pgadmissions.domain.NotificationTemplateVersion;
import com.zuehlke.pgadmissions.domain.PrismResource;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.DurationUnitEnum;
import com.zuehlke.pgadmissions.domain.enums.PrismAction;
import com.zuehlke.pgadmissions.domain.enums.PrismNotificationTemplate;
import com.zuehlke.pgadmissions.domain.enums.PrismRole;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;
import com.zuehlke.pgadmissions.dto.ApplicationExportConfigurationDTO;
import com.zuehlke.pgadmissions.dto.ServiceLevelsDTO;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.services.ApplicationExportConfigurationService;
import com.zuehlke.pgadmissions.services.ConfigurationService;
import com.zuehlke.pgadmissions.services.EntityService;
import com.zuehlke.pgadmissions.services.NotificationTemplateService;
import com.zuehlke.pgadmissions.services.ProgramService;
import com.zuehlke.pgadmissions.services.RoleService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;

@Controller
@RequestMapping("/configuration")
public class ConfigurationController {

    private static final String CONFIGURATION_VIEW_NAME = "/private/staff/superAdmin/configuration";

    private static final String CONFIGURATION_SECTION_NAME = "/private/staff/superAdmin/configuration_section";

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ApplicationExportConfigurationService applicationExportConfigurationService;

    @Autowired
    private NotificationTemplateService templateService;

    @Autowired
    private ProgramService programsService;

    @Autowired
    private NotificationTemplateService notificationService;

    @Autowired
    private ScoringDefinitionParser scoringDefinitionParser;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EntityService entityService;

    @RequestMapping(method = RequestMethod.GET)
    public String getConfigurationPage() {
        User user = userService.getCurrentUser();
        if (!roleService.hasRole(user, PrismRole.SYSTEM_ADMINISTRATOR) && !roleService.hasRole(user, PrismRole.PROGRAM_ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        return CONFIGURATION_VIEW_NAME;
    }

    @RequestMapping(method = RequestMethod.GET, value = "config_section")
    public String getConfigurationSection() {
        // FIXME rewrite using AJAX
        if (!roleService.hasRole(getUser(), PrismRole.SYSTEM_ADMINISTRATOR)) {
            return "/private/common/simpleMessage";
        }
        return CONFIGURATION_SECTION_NAME;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String submit(@ModelAttribute ServiceLevelsDTO serviceLevelsDTO) {
        User user = userService.getCurrentUser();
        if (!roleService.hasRole(user, PrismRole.SYSTEM_ADMINISTRATOR)) {
            throw new ResourceNotFoundException();
        }
        configurationService.saveServiceLevels(serviceLevelsDTO);
        return "redirect:/configuration/config_section";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{id:\\d+}")
    @ResponseBody
    public Map<String, Object> getTemplateVersion(@PathVariable Integer id) {
        NotificationTemplateVersion template = templateService.getTemplateVersionById(id);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("content", template.getContent());
        result.put("createdTimestamp", template.getCreatedTimestamp());
        result.put("subject", template.getSubject());
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getThrottle")
    @ResponseBody
    public Map<String, Object> getApplicationExportConfiguration() {
        ApplicationExportConfigurationDTO dto = applicationExportConfigurationService.getApplicationExportConfiguration();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("enabled", dto.isEnabled());
        result.put("batchSize", dto.getBatchSize());
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/updateThrottle")
    @ResponseBody
    public Map<String, Object> updateApplicationExportConfiguration(@Valid ApplicationExportConfigurationDTO configuration, BindingResult throttleErrors) {

        Map<String, Object> errorsMap = FieldErrorUtils.populateMapWithErrors(throttleErrors, applicationContext);
        if (!errorsMap.isEmpty()) {
            return errorsMap;
        }

        applicationExportConfigurationService.updateApplicationExportConfiguration(configuration);

        if (applicationExportConfigurationService.userTurnedOnThrottle(configuration.isEnabled())) {
            // queueService.sendQueuedApprovedApplicationsToPortico();
        }

        return Collections.emptyMap();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/editScoringDefinition")
    @ResponseBody
    public Map<String, String> editScoringDefinition(@RequestParam Integer programId, @RequestParam PrismAction actionId, @RequestParam String definition) {
        Map<String, String> errors = validateScoringDefinition(programId, definition);
        if (errors.isEmpty()) {
            if (definition.equals("")) {
                programsService.disableCustomQuestionsForProgram(programId, actionId);
            } else {
                programsService.createCustomQuestionsForProgram(programId, actionId, definition);
            }
        }
        return errors;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getScoringDefinition")
    @ResponseBody
    public String getScoringDefinition(@RequestParam Integer programId, @RequestParam PrismAction actionId) {
        CommentCustomQuestion customQuestions = programsService.getCustomQuestionsForProgram(programId, actionId);
        if (customQuestions != null) {
            return customQuestions.getVersion().getContent();
        } else {
            return "";
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/editEmailTemplate/{resourceScope:[A-Z_]+}/{resourceId:[0-9]+}/{templateId:[a-zA-Z_]+}")
    @ResponseBody
    public Map<Object, Object> getVersionsForTemplate(@PathVariable PrismScope resourceScope, @PathVariable Integer resourceId, @PathVariable String templateId) {
        try {
            PrismResource resource = (PrismResource) entityService.getById(Class.forName(resourceScope.getSimpleName()), resourceId);
            NotificationTemplate template = templateService.getById(valueOf(templateId));
            List<NotificationTemplateVersion> versions = notificationService.getVersionsForTemplate(resource, template);
            Map<Object, Object> result = new HashMap<Object, Object>();
            result.put("activeVersion", notificationService.getActiveVersionForTemplate(resource, template));
            result.put("versions", versions);
            return result;
        } catch (ClassNotFoundException e) {
            throw new Error("Tried to fetch notication template versions for invalid prism resource", e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = { "saveEmailTemplate/{resourceScope:[A-Z_]+}/{resourceId:[0-9]+}/{templateId:[a-zA-Z_]+}" })
    @ResponseBody
    public Map<String, Object> saveTemplate(@PathVariable PrismScope resourceScope, @PathVariable Integer resourceId,
            @PathVariable PrismNotificationTemplate templateId, @RequestParam String content, @RequestParam String subject) {
        try {
            PrismResource resource = (PrismResource) entityService.getById(Class.forName(resourceScope.getSimpleName()), resourceId);
            NotificationTemplateVersion newVersion = templateService.saveTemplateVersion(resource, templateId, content, subject);
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("id", newVersion.getId());
            result.put("createdTimestamp", new SimpleDateFormat("yyyy/M/d - HH:mm:ss").format(newVersion.getCreatedTimestamp()));
            return result;
        } catch (ClassNotFoundException e) {
            throw new Error("Tried to create notication template version for invalid prism resource", e);
        }
    }

    @ModelAttribute("templateTypes")
    public PrismNotificationTemplate[] getTemplateTypes() {
        PrismNotificationTemplate[] names = PrismNotificationTemplate.values();
        sort(names, new Comparator<PrismNotificationTemplate>() {
            @Override
            public int compare(PrismNotificationTemplate o1, PrismNotificationTemplate o2) {
                return o1.displayValue().compareTo(o2.displayValue());
            }
        });
        return names;
    }

    @ModelAttribute("states")
    public List<State> getConfigurableStates() {
        return configurationService.getConfigurableStates();
    }

    @ModelAttribute("user")
    public User getUser() {
        return userService.getCurrentUser();
    }

    @ModelAttribute("units")
    public DurationUnitEnum[] getUnits() {
        return DurationUnitEnum.values();
    }

    @ModelAttribute("programs")
    public List<Program> getPrograms() {
        // TODO: We can write this as a single query now
        User user = userService.getCurrentUser();
        if (roleService.hasRole(user, PrismRole.SYSTEM_ADMINISTRATOR)) {
            return programsService.getAllEnabledPrograms();
        }
        return roleService.getProgramsByUserAndRole(user, PrismRole.PROGRAM_ADMINISTRATOR);
    }

    private Map<String, String> validateScoringDefinition(Integer programId, String scoringContent) {
        Program program = programsService.getById(programId).getProgram();
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

    @RequestMapping(method = RequestMethod.POST, value = "/fakeSubmitScores")
    public String dummySubmitScores(@ModelAttribute("dummyComment") Comment dummyComment, BindingResult result, @RequestParam String scoringContent, Model model)
            throws ScoringDefinitionParseException {
        // List<Score> scores = dummyComment.getScores();
        // CustomQuestions parseScoringDefinition = scoringDefinitionParser.parseScoringDefinition(scoringContent);
        // model.addAttribute("scores", scores);
        // model.addAttribute("alertForScoringQuestions", parseScoringDefinition.getAlert());
        // if (scores != null) {
        // List<Question> questions = parseScoringDefinition.getQuestion();
        // for (int i = 0; i < scores.size(); i++) {
        // Score score = scores.get(i);
        // score.setOriginalQuestion(questions.get(i));
        // }
        // }
        // dummyCommentValidator.validate(dummyComment, result);
        // model.addAttribute("errorsContainerName", "dummyComment");
        return "/private/staff/scores";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/previewScoringDefinition")
    public String previewScoringDefinition(@RequestParam String programCode, @RequestParam String scoringContent, Model model, HttpServletResponse response)
            throws IOException {
        // String errorMessage = "";
        // try {
        // CustomQuestions parseScoringDefinition = scoringDefinitionParser.parseScoringDefinition(scoringContent);
        // List<Score> scores = scoreFactory.createScores(parseScoringDefinition.getQuestion());
        // model.addAttribute("scores", scores);
        // model.addAttribute("alertForScoringQuestions", parseScoringDefinition.getAlert());
        // return "/private/staff/scores";
        // } catch (ScoringDefinitionParseException e) {
        // errorMessage = e.getLocalizedMessage();
        // }
        //
        // response.sendError(418, errorMessage);
        return null;
    }
}