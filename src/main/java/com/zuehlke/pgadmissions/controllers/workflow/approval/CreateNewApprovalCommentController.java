package com.zuehlke.pgadmissions.controllers.workflow.approval;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalComment;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.CommentType;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.services.ApplicationsService;
import com.zuehlke.pgadmissions.services.ApprovalService;
import com.zuehlke.pgadmissions.services.CommentService;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.FieldErrorUtils;
import com.zuehlke.pgadmissions.validators.ApprovalCommentValidator;

@Controller
public class CreateNewApprovalCommentController {

    private final ApplicationsService applicationsService;
    
    private final UserService userService;
    
    private final ApprovalService approvalService;
    
    private final CommentService commentService;
    
    private final ApprovalCommentValidator validator;
    
    private final MessageSource messageSource;
    
    public CreateNewApprovalCommentController() {
        this(null, null, null, null, null, null);
    }

    @Autowired
    public CreateNewApprovalCommentController(
            final ApplicationsService applicationsService,
            final UserService userService,
            final ApprovalService approvalService,
            final CommentService commentService,
            final ApprovalCommentValidator validator,
            final MessageSource messageSource) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.validator = validator;
        this.approvalService = approvalService;
        this.commentService = commentService;
        this.messageSource = messageSource;
    }
    
    @RequestMapping(value = "/applications/{applicationNumber}/approvalRound/latest/comment", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String get(final @PathVariable("applicationNumber") String applicationNumber) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.registerTypeAdapter(ApprovalRound.class, new JsonSerializer<ApprovalRound>() {
            @Override
            public JsonElement serialize(ApprovalRound src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("projectTitle", src.getProjectTitle());
                jsonObject.addProperty("projectAbstract", src.getProjectAbstract());
                jsonObject.addProperty("projectDescriptionAvailable", BooleanUtils.toString(src.getProjectDescriptionAvailable(), "true", "false", StringUtils.EMPTY));
                jsonObject.addProperty("recommendedConditionsAvailable", BooleanUtils.toString(src.getRecommendedConditionsAvailable(), "true", "false", StringUtils.EMPTY));
                jsonObject.addProperty("recommendedConditions", src.getRecommendedConditions());
                jsonObject.addProperty("recommendedStartDate", new DateTime(src.getRecommendedStartDate()).toString("dd MMM yyyy"));
                return jsonObject;
            }
        }).create();
        
        RegisteredUser currentUser = getCurrentUser();
        ApplicationForm form = applicationsService.getApplicationByApplicationNumber(applicationNumber);
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();

        if (!currentUser.hasAdminRightsOnApplication(form) && !currentUser.isInRoleInProgram(Authority.APPROVER, form.getProgram())) {
            throw new InsufficientApplicationFormPrivilegesException(form.getApplicationNumber());
        }
        
        if (latestApprovalRound != null) {
            return gson.toJson(latestApprovalRound, ApprovalRound.class);
        }
        
        return gson.toJson(Collections.singletonMap("success", true));
    }
    
    @RequestMapping(value = "/applications/{applicationNumber}/approvalRound/latest/comment/validate", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String validate(final @PathVariable("applicationNumber") String applicationNumber,
            @Valid final ApprovalComment approvalComment, @RequestParam final String comment,
            @RequestParam final String confirmNextStage, final BindingResult bindingResult) {
        Gson gson = new Gson();
        Map<String, Object> fieldErrorMap = new HashMap<String, Object>();
        RegisteredUser currentUser = getCurrentUser();
        ApplicationForm form = applicationsService.getApplicationByApplicationNumber(applicationNumber);

        if (!currentUser.hasAdminRightsOnApplication(form) && !currentUser.isInRoleInProgram(Authority.APPROVER, form.getProgram())) {
            throw new InsufficientApplicationFormPrivilegesException(form.getApplicationNumber());
        }
        
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setApplication(form);
        approvalComment.setComment(StringUtils.EMPTY);
        approvalComment.setConfirmNextStage(true);
        approvalComment.setDate(new Date());
        approvalComment.setUser(currentUser);
        
        validator.validate(approvalComment, bindingResult);
        if (bindingResult.hasErrors()) {
            fieldErrorMap = FieldErrorUtils.populateMapWithErrors(bindingResult, messageSource);
        }
        
        if (StringUtils.isEmpty(comment)) {
            fieldErrorMap.put("comment", FieldErrorUtils.resolveMessage("text.field.empty", messageSource));
        }
        
        if (StringUtils.equalsIgnoreCase("false", confirmNextStage)) {
            fieldErrorMap.put("confirmNextStage", FieldErrorUtils.resolveMessage("checkbox.mandatory", messageSource));
        }
        
        if (!fieldErrorMap.isEmpty()) {
            fieldErrorMap.put("success", false);
            return gson.toJson(fieldErrorMap);
        }
        
        return gson.toJson(Collections.singletonMap("success", true));
    }
    
    @RequestMapping(value = "/applications/{applicationNumber}/approvalRound/latest/comment", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String save(final @PathVariable("applicationNumber") String applicationNumber, @Valid final ApprovalComment approvalComment, final BindingResult bindingResult) {
        
        Gson gson = new Gson();
        RegisteredUser currentUser = getCurrentUser();
        ApplicationForm form = applicationsService.getApplicationByApplicationNumber(applicationNumber);
        ApprovalRound latestApprovalRound = form.getLatestApprovalRound();

        if (!currentUser.hasAdminRightsOnApplication(form) && !currentUser.isInRoleInProgram(Authority.APPROVER, form.getProgram())) {
            throw new InsufficientApplicationFormPrivilegesException(form.getApplicationNumber());
        }
        
        approvalComment.setType(CommentType.APPROVAL);
        approvalComment.setApplication(form);
        approvalComment.setComment(StringUtils.EMPTY);
        approvalComment.setConfirmNextStage(true);
        approvalComment.setDate(new Date());
        approvalComment.setUser(currentUser);
        
        latestApprovalRound.setProjectAbstract(approvalComment.getProjectAbstract());
        latestApprovalRound.setProjectDescriptionAvailable(approvalComment.getProjectDescriptionAvailable());
        latestApprovalRound.setProjectTitle(approvalComment.getProjectTitle());
        latestApprovalRound.setRecommendedConditions(approvalComment.getRecommendedConditions());
        latestApprovalRound.setRecommendedConditionsAvailable(approvalComment.getRecommendedConditionsAvailable());
        latestApprovalRound.setRecommendedStartDate(approvalComment.getRecommendedStartDate());
        latestApprovalRound.setProjectDescriptionAvailable(approvalComment.getProjectDescriptionAvailable());
        
        approvalService.save(latestApprovalRound);
        commentService.save(approvalComment);
        
        return gson.toJson(Collections.singletonMap("success", true));
    }
    
    private RegisteredUser getCurrentUser() {
        return userService.getCurrentUser();
    }
}
