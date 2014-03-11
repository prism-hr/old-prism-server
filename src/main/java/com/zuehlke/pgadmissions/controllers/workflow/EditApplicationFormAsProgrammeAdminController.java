package com.zuehlke.pgadmissions.controllers.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.zuehlke.pgadmissions.components.ActionsProvider;
import com.zuehlke.pgadmissions.controllers.factory.ScoreFactory;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.ReferenceComment;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.domain.ScoringDefinition;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormAction;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.dto.RefereesAdminEditDTO;
import com.zuehlke.pgadmissions.dto.SendToPorticoDataDTO;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;
import com.zuehlke.pgadmissions.propertyeditors.DocumentPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.DomicilePropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.ScoresPropertyEditor;
import com.zuehlke.pgadmissions.propertyeditors.SendToPorticoDataDTOEditor;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParser;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.services.ApplicationFormUserRoleService;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.DomicileService;
import com.zuehlke.pgadmissions.services.RefereeService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.validators.RefereesAdminEditDTOValidator;

@Controller
@RequestMapping("/editApplicationFormAsProgrammeAdmin")
public class EditApplicationFormAsProgrammeAdminController {
    // TODO fix tests

    private static final Logger log = LoggerFactory.getLogger(EditApplicationFormAsProgrammeAdminController.class);

    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME = "/private/staff/admin/application/main_application_page_programme_administrator";

    private static final String VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME = "/private/staff/admin/application/components/references_details_programme_admin";

    @Autowired
    protected UserService userService;

    @Autowired
    protected ApplicationsService applicationsService;

    @Autowired
    protected DocumentPropertyEditor documentPropertyEditor;

    @Autowired
    protected RefereeService refereeService;
    
    @Autowired
    protected RefereesAdminEditDTOValidator refereesAdminEditDTOValidator;
    
    
    @Autowired
    protected SendToPorticoDataDTOEditor sendToPorticoDataDTOEditor;

    @Autowired
    protected EncryptionHelper encryptionHelper;

    @Autowired
    protected DomicileService domicileService;

    @Autowired
    protected DomicilePropertyEditor domicilePropertyEditor;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    protected ScoringDefinitionParser scoringDefinitionParser;

    @Autowired
    protected ScoresPropertyEditor scoresPropertyEditor;
    
    @Autowired
    protected ScoreFactory scoreFactory;

    @Autowired
    protected ApplicationFormUserRoleService applicationFormUserRoleService;
    
    @Autowired
    protected ActionsProvider actionsProvider;

    @InitBinder(value = "sendToPorticoData")
    public void registerSendToPorticoData(WebDataBinder binder) {
        binder.registerCustomEditor(List.class, sendToPorticoDataDTOEditor);
    }

    @InitBinder(value = "refereesAdminEditDTO")
    public void registerPropertyEditors(WebDataBinder binder) {
        binder.setValidator(refereesAdminEditDTOValidator);
        binder.registerCustomEditor(Document.class, documentPropertyEditor);
        binder.registerCustomEditor(Domicile.class, domicilePropertyEditor);
        binder.registerCustomEditor(null, "comment", new StringTrimmerEditor("\r", true));
        binder.registerCustomEditor(String.class, newStringTrimmerEditor());
        binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor());
        binder.registerCustomEditor(null, "scores", scoresPropertyEditor);
    }

    @RequestMapping(method = RequestMethod.GET)
    public String view(@ModelAttribute ApplicationForm applicationForm) {
    	actionsProvider.validateAction(applicationForm, getCurrentUser(), ApplicationFormAction.VIEW_EDIT);
    	applicationFormUserRoleService.deleteApplicationUpdate(applicationForm, getCurrentUser());
        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_VIEW_NAME;
    }

    @RequestMapping(value = "/editReferenceData", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String updateReference(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO,
            BindingResult refereesAdminEditDTOResult, ModelMap modelMap) throws ScoringDefinitionParseException {
        String editedRefereeId = refereesAdminEditDTO.getEditedRefereeId();
        modelMap.addAttribute("editedRefereeId", editedRefereeId);

        createScoresWithQuestion(applicationForm, refereesAdminEditDTO);
        refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, refereesAdminEditDTOResult);

        Map<String, Object> map = new HashMap<String, Object>();
        if (!refereesAdminEditDTOResult.hasErrors()) {
            refereeService.editReferenceComment(applicationForm, refereesAdminEditDTO);
            map.put("success", "true");
        } else {
            map.put("success", "false");
            map.putAll(FieldErrorUtils.populateMapWithErrors(refereesAdminEditDTOResult, messageSource));
        }
        
        applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/postRefereesData", method = RequestMethod.POST)
    public String submitRefereesData(@ModelAttribute ApplicationForm applicationForm, @ModelAttribute RefereesAdminEditDTO refereesAdminEditDTO,
            BindingResult referenceResult, @ModelAttribute("sendToPorticoData") SendToPorticoDataDTO sendToPorticoData,
            @RequestParam(required = false) Boolean forceSavingReference, Model model) throws ScoringDefinitionParseException {

        String editedRefereeId = refereesAdminEditDTO.getEditedRefereeId();
        model.addAttribute("editedRefereeId", editedRefereeId);
        if (sendToPorticoData.getRefereesSendToPortico() != null) {
            refereeService.selectForSendingToPortico(applicationForm, sendToPorticoData.getRefereesSendToPortico());
        }

        if (!"newReferee".equals(editedRefereeId)) {
            Integer decryptedId = encryptionHelper.decryptToInteger(editedRefereeId);
            Referee referee = refereeService.getRefereeById(decryptedId);
            if (referee.getReference() != null) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }
        }

        createScoresWithQuestion(applicationForm, refereesAdminEditDTO);

        if (BooleanUtils.isTrue(forceSavingReference) || refereesAdminEditDTO.hasUserStartedTyping()
                || (BooleanUtils.isTrue(forceSavingReference) && BooleanUtils.isFalse(refereesAdminEditDTO.getContainsRefereeData()))) {

            refereesAdminEditDTOValidator.validate(refereesAdminEditDTO, referenceResult);

            if (referenceResult.hasErrors()) {
                return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
            }

            
            ReferenceComment newComment = refereeService.postCommentOnBehalfOfReferee(applicationForm, refereesAdminEditDTO);
            
            // TODO obtain referee (can be created new one)
            Referee referee = null;
//            Referee referee = newComment.getReferee();
//            applicationsService.refresh(applicationForm);
//            refereeService.refresh(referee);

            applicationsService.save(applicationForm);
            applicationFormUserRoleService.referencePosted(newComment);
            applicationFormUserRoleService.insertApplicationUpdate(applicationForm, getCurrentUser(), ApplicationUpdateScope.ALL_USERS);

            String newRefereeId = encryptionHelper.encrypt(referee.getId());
            model.addAttribute("editedRefereeId", newRefereeId);
        }

        return VIEW_APPLICATION_PROGRAMME_ADMINISTRATOR_REFERENCES_VIEW_NAME;
    }

    public List<Question> getCustomQuestions(ApplicationForm applicationForm) throws ScoringDefinitionParseException {
        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
            return customQuestion.getQuestion();
        }
        return null;
    }

    @ModelAttribute(value = "refereesAdminEditDTO")
    public RefereesAdminEditDTO getRefereesAdminEditDTO(@RequestParam String applicationId) throws ScoringDefinitionParseException {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RefereesAdminEditDTO refereesAdminEditDTO = new RefereesAdminEditDTO();

        ScoringDefinition scoringDefinition = applicationForm.getProgram().getScoringDefinitions().get(ScoringStage.REFERENCE);
        if (scoringDefinition != null) {
            try {
                CustomQuestions customQuestion = scoringDefinitionParser.parseScoringDefinition(scoringDefinition.getContent());
                List<Score> scores = scoreFactory.createScores(customQuestion.getQuestion());
                refereesAdminEditDTO.getScores().addAll(scores);
                refereesAdminEditDTO.setAlert(customQuestion.getAlert());
            } catch (ScoringDefinitionParseException e) {
                log.error("Incorrect scoring XML configuration for reference stage in program: " + applicationForm.getAdvert().getTitle());
            }
        }
        return refereesAdminEditDTO;
    }

    @ModelAttribute("domiciles")
    public List<Domicile> getAllDomiciles() {
        return domicileService.getAllEnabledDomiciles();
    }

    @ModelAttribute("user")
    public RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }

    public StringTrimmerEditor newStringTrimmerEditor() {
        return new StringTrimmerEditor(false);
    }

    @ModelAttribute
    public ApplicationForm getApplicationForm(@RequestParam String applicationId) {
        ApplicationForm applicationForm = applicationsService.getApplicationByApplicationNumber(applicationId);
        if (applicationForm == null) {
            throw new MissingApplicationFormException(applicationId);
        }
        return applicationForm;
    }

    @ModelAttribute("applicationDescriptor")
    public ApplicationDescriptor getApplicationDescriptor(@RequestParam String applicationId) {
        ApplicationForm applicationForm = getApplicationForm(applicationId);
        RegisteredUser user = getCurrentUser();
        return actionsProvider.getApplicationDescriptorForUser(applicationForm, user);
    }

    public void createScoresWithQuestion(ApplicationForm applicationForm, RefereesAdminEditDTO refereesAdminEditDTO) throws ScoringDefinitionParseException {
        List<Score> scores = refereesAdminEditDTO.getScores();
        if (!scores.isEmpty()) {
            List<Question> questions = getCustomQuestions(applicationForm);
            for (int i = 0; i < scores.size(); i++) {
                Score score = scores.get(i);
                if (i < questions.size()) {
                    score.setOriginalQuestion(questions.get(i));
                }
            }
        }
    }

}