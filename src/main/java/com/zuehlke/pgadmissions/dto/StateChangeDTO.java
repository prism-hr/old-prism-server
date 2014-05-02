package com.zuehlke.pgadmissions.dto;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.domain.enums.HomeOrOverseas;
import com.zuehlke.pgadmissions.domain.enums.ScoringStage;
import com.zuehlke.pgadmissions.domain.enums.ValidationQuestionOptions;
import com.zuehlke.pgadmissions.validators.ESAPIConstraint;

public class StateChangeDTO {

    private String action;

    private User user;

    private ApplicationForm applicationForm;

    private String comment;

    private List<Document> documents;

    private ValidationQuestionOptions qualifiedForPhd;

    private ValidationQuestionOptions englishCompentencyOk;

    private HomeOrOverseas homeOrOverseas;

    private PrismState status;

    private PrismState nextStatus;

    private Boolean fastTrackApplication;

    private Boolean delegate;

    private String delegateFirstName;

    private String delegateLastName;

    @ESAPIConstraint(rule = "Email", maxLength = 255, message = "{text.email.notvalid}")
    private String delegateEmail;

    private Boolean confirmNextStage;

    private ValidationQuestionOptions[] validationQuestionOptions;

    private HomeOrOverseas[] homeOrOverseasOptions;

    private List<PrismState> stati;

    private Boolean useCustomReferenceQuestions;

    private Boolean useCustomQuestions;

    public StateChangeDTO() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ApplicationForm getApplicationForm() {
        return applicationForm;
    }

    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
        this.status = applicationForm.getState().getId();
    }

    public String getApplicationNumber() {
        return applicationForm.getApplicationNumber();
    }

    public Boolean hasFastTrackOption() {
        if (applicationForm.getClosingDate() == null) {
            return false;
        }
        return true;
    }

    public Boolean isInState(String applicationFormStatus) {
        return applicationForm.getState().getId().toString().equals(applicationFormStatus);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public ValidationQuestionOptions getQualifiedForPhd() {
        return qualifiedForPhd;
    }

    public void setQualifiedForPhd(ValidationQuestionOptions qualifiedForPhD) {
        this.qualifiedForPhd = qualifiedForPhD;
    }

    public ValidationQuestionOptions getEnglishCompentencyOk() {
        return englishCompentencyOk;
    }

    public void setEnglishCompentencyOk(ValidationQuestionOptions englishCompentencyOk) {
        this.englishCompentencyOk = englishCompentencyOk;
    }

    public HomeOrOverseas getHomeOrOverseas() {
        return homeOrOverseas;
    }

    public void setHomeOrOverseas(HomeOrOverseas homeOrOverseas) {
        this.homeOrOverseas = homeOrOverseas;
    }

    public PrismState getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(PrismState nextStatus) {
        this.nextStatus = nextStatus;
    }

    public Boolean getFastTrackApplication() {
        return fastTrackApplication;
    }

    public void setFastTrackApplication(Boolean fastTrackApplication) {
        this.fastTrackApplication = fastTrackApplication;
    }

    public Boolean getDelegate() {
        return delegate;
    }

    public void setDelegate(Boolean delegate) {
        this.delegate = delegate;
    }

    public String getDelegateFirstName() {
        return delegateFirstName;
    }

    public void setDelegateFirstName(String delegateFirstName) {
        this.delegateFirstName = delegateFirstName;
    }

    public String getDelegateLastName() {
        return delegateLastName;
    }

    public void setDelegateLastName(String delegateLastName) {
        this.delegateLastName = delegateLastName;
    }

    public String getDelegateEmail() {
        return delegateEmail;
    }

    public void setDelegateEmail(String delegateEmail) {
        this.delegateEmail = delegateEmail;
    }

    public Boolean getConfirmNextStage() {
        return confirmNextStage;
    }

    public void setConfirmNextStage(Boolean confirmNextStage) {
        this.confirmNextStage = confirmNextStage;
    }

    public ValidationQuestionOptions[] getValidationQuestionOptions() {
        return validationQuestionOptions;
    }

    public void setValidationQuestionOptions(ValidationQuestionOptions[] validationQuestionOptions) {
        this.validationQuestionOptions = validationQuestionOptions;
    }

    public HomeOrOverseas[] getHomeOrOverseasOptions() {
        return homeOrOverseasOptions;
    }

    public void setHomeOrOverseasOptions(HomeOrOverseas[] homeOrOverseasOptions) {
        this.homeOrOverseasOptions = homeOrOverseasOptions;
    }

    public List<PrismState> getStati() {
        return stati;
    }

    public void setStati(List<PrismState> stati) {
        this.stati = stati;
    }

    public Boolean getUseCustomReferenceQuestions() {
        return useCustomReferenceQuestions;
    }

    public void setUseCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
    }

    public Boolean getUseCustomQuestions() {
        return useCustomQuestions;
    }

    public void setUseCustomQuestions(Boolean useCustomQuestions) {
        this.useCustomQuestions = useCustomQuestions;
    }

    public List<ScoringStage> getCustomQuestionCoverage() {
        return applicationForm.getProgram().getCustomQuestionCoverage();
    }

    public boolean displayCustomQuestionsOption() {
        return (this.nextStatus == PrismState.APPLICATION_REVIEW && getCustomQuestionCoverage().contains(ScoringStage.REVIEW))
                || (this.nextStatus == PrismState.APPLICATION_INTERVIEW && getCustomQuestionCoverage().contains(ScoringStage.INTERVIEW));
    }

    public boolean displayCustomReferenceQuestionsOption() {
        return this.status == PrismState.APPLICATION_VALIDATION && getCustomQuestionCoverage().contains(ScoringStage.REFERENCE);
    }

}
