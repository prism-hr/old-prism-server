package com.zuehlke.pgadmissions.rest.representation.comment;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;

public class CommentRepresentation {

    private Integer id;

    private UserRepresentation user;

    private UserRepresentation delegateUser;

    private PrismAction action;

    private Boolean declinedResponse;

    private String content;

    private PrismState transitionState;

    private PrismYesNoUnsureResponse applicationEligible;

    private Boolean applicationInterested;
    
    private InstitutionRepresentation partner;
    
    private Boolean removedPartner;
    
    private CommentSponsorshipRepresentation sponsorship;

    private CommentApplicationInterviewAppointmentRepresentation interviewAppointment;

    private CommentApplicationInterviewInstructionRepresentation interviewInstruction;

    private CommentApplicationPositionDetailRepresentation positionDetail;

    private CommentApplicationOfferDetailRepresentation offerDetail;

    private Boolean recruiterAcceptAppointment;

    private PrismApplicationReserveStatus applicationReserveStatus;

    private String rejectionReason;

    private String rejectionReasonSystem;

    private BigDecimal applicationRating;

    private Boolean useCustomRefereeQuestions;

    private Boolean useCustomRecruiterQuestions;

    private Integer customQuestionVersionId;

    private String customQuestionResponse;

    private String exportRequest;

    private String exportResponse;

    private String exportException;

    private String exportReference;

    private DateTime createdTimestamp;

    private List<CommentAssignedUserRepresentation> assignedUsers;

    private List<AppointmentTimeslotRepresentation> appointmentTimeslots;

    private List<AppointmentPreferenceRepresentation> appointmentPreferences;

    private List<FileRepresentation> documents;

    private List<CommentCustomResponseRepresentation> customResponses;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public UserRepresentation getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
    }

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }
    
    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PrismState getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
    }

    public PrismYesNoUnsureResponse getApplicationEligible() {
        return applicationEligible;
    }

    public void setApplicationEligible(PrismYesNoUnsureResponse applicationEligible) {
        this.applicationEligible = applicationEligible;
    }

    public Boolean getApplicationInterested() {
        return applicationInterested;
    }

    public void setApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
    }

    public InstitutionRepresentation getPartner() {
        return partner;
    }

    public void setPartner(InstitutionRepresentation partner) {
        this.partner = partner;
    }
    
    public Boolean getRemovedPartner() {
        return removedPartner;
    }

    public void setRemovedPartner(Boolean removedPartner) {
        this.removedPartner = removedPartner;
    }

    public CommentSponsorshipRepresentation getSponsorship() {
        return sponsorship;
    }

    public void setSponsorship(CommentSponsorshipRepresentation sponsorship) {
        this.sponsorship = sponsorship;
    }

    public final CommentApplicationInterviewAppointmentRepresentation getInterviewAppointment() {
        return interviewAppointment;
    }

    public final void setInterviewAppointment(CommentApplicationInterviewAppointmentRepresentation interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public final CommentApplicationInterviewInstructionRepresentation getInterviewInstruction() {
        return interviewInstruction;
    }

    public final void setInterviewInstruction(CommentApplicationInterviewInstructionRepresentation interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public CommentApplicationPositionDetailRepresentation getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(CommentApplicationPositionDetailRepresentation positionDetail) {
        this.positionDetail = positionDetail;
    }

    public CommentApplicationOfferDetailRepresentation getOfferDetail() {
        return offerDetail;
    }

    public void setOfferDetail(CommentApplicationOfferDetailRepresentation offerDetail) {
        this.offerDetail = offerDetail;
    }

    public Boolean getRecruiterAcceptAppointment() {
        return recruiterAcceptAppointment;
    }

    public void setRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
    }

    public PrismApplicationReserveStatus getApplicationReserveStatus() {
		return applicationReserveStatus;
	}

	public void setApplicationReserveStatus(PrismApplicationReserveStatus applicationReserveStatus) {
		this.applicationReserveStatus = applicationReserveStatus;
	}

	public final String getRejectionReason() {
        return rejectionReason;
    }

    public final void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public final String getRejectionReasonSystem() {
        return rejectionReasonSystem;
    }

    public final void setRejectionReasonSystem(String rejectionReasonSystem) {
        this.rejectionReasonSystem = rejectionReasonSystem;
    }

    public final BigDecimal getApplicationRating() {
        return applicationRating;
    }

    public final void setApplicationRating(BigDecimal applicationRating) {
        this.applicationRating = applicationRating;
    }

    public Boolean getUseCustomRefereeQuestions() {
        return useCustomRefereeQuestions;
    }

    public void setUseCustomRefereeQuestions(Boolean useCustomRefereeQuestions) {
        this.useCustomRefereeQuestions = useCustomRefereeQuestions;
    }

    public Boolean getUseCustomRecruiterQuestions() {
        return useCustomRecruiterQuestions;
    }

    public void setUseCustomRecruiterQuestions(Boolean useCustomRecruiterQuestions) {
        this.useCustomRecruiterQuestions = useCustomRecruiterQuestions;
    }

    public Integer getCustomQuestionVersionId() {
        return customQuestionVersionId;
    }

    public void setCustomQuestionVersionId(Integer customQuestionVersionId) {
        this.customQuestionVersionId = customQuestionVersionId;
    }

    public String getCustomQuestionResponse() {
        return customQuestionResponse;
    }

    public void setCustomQuestionResponse(String customQuestionResponse) {
        this.customQuestionResponse = customQuestionResponse;
    }

    public String getExportRequest() {
        return exportRequest;
    }

    public void setExportRequest(String exportRequest) {
        this.exportRequest = exportRequest;
    }

    public String getExportResponse() {
        return exportResponse;
    }

    public void setExportResponse(String exportResponse) {
        this.exportResponse = exportResponse;
    }

    public String getExportException() {
        return exportException;
    }

    public void setExportException(String exportException) {
        this.exportException = exportException;
    }

    public String getExportReference() {
        return exportReference;
    }

    public void setExportReference(String exportReference) {
        this.exportReference = exportReference;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public List<CommentAssignedUserRepresentation> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<CommentAssignedUserRepresentation> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<AppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<AppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<AppointmentPreferenceRepresentation> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<AppointmentPreferenceRepresentation> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public List<FileRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FileRepresentation> documents) {
        this.documents = documents;
    }

    public List<CommentCustomResponseRepresentation> getCustomResponses() {
        return customResponses;
    }

    public void setCustomResponses(List<CommentCustomResponseRepresentation> customResponses) {
        this.customResponses = customResponses;
    }

    public CommentRepresentation addId(Integer id) {
        this.id = id;
        return this;
    }

    public CommentRepresentation addUser(UserRepresentation user) {
        this.user = user;
        return this;
    }

    public CommentRepresentation addDelegateUser(UserRepresentation delegateUser) {
        this.delegateUser = delegateUser;
        return this;
    }

    public CommentRepresentation addAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public CommentRepresentation addDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
        return this;
    }

    public CommentRepresentation addCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

}
