package com.zuehlke.pgadmissions.rest.representation.comment;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.user.UserRepresentationSimple;

public class CommentRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private UserRepresentationSimple delegateUser;

    private PrismAction action;

    private Boolean declinedResponse;

    private String content;

    private PrismState state;

    private PrismState transitionState;

    private BigDecimal rating;

    private Boolean applicationIdentified;

    private PrismYesNoUnsureResponse applicationEligible;

    private Boolean applicationInterested;

    private CommentInterviewAppointmentRepresentation interviewAppointment;

    private CommentInterviewInstructionRepresentation interviewInstruction;

    private CommentPositionDetailRepresentation positionDetail;

    private CommentOfferDetailRepresentation offerDetail;

    private Boolean recruiterAcceptAppointment;

    private PrismApplicationReserveStatus applicationReserveStatus;

    private String rejectionReason;

    private String rejectionReasonSystem;

    private DateTime createdTimestamp;

    private List<CommentAssignedUserRepresentation> assignedUsers;

    private List<CommentCompetenceRepresentation> competences;

    private List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots;

    private List<LocalDateTime> appointmentPreferences;

    private List<DocumentRepresentation> documents;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserRepresentationSimple getUser() {
        return user;
    }

    public void setUser(UserRepresentationSimple user) {
        this.user = user;
    }

    public UserRepresentationSimple getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(UserRepresentationSimple delegateUser) {
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

    public PrismState getState() {
        return state;
    }

    public void setState(PrismState state) {
        this.state = state;
    }

    public PrismState getTransitionState() {
        return transitionState;
    }

    public void setTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
    }

    public Boolean getApplicationIdentified() {
        return applicationIdentified;
    }

    public void setApplicationIdentified(Boolean applicationIdentified) {
        this.applicationIdentified = applicationIdentified;
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

    public CommentInterviewAppointmentRepresentation getInterviewAppointment() {
        return interviewAppointment;
    }

    public void setInterviewAppointment(CommentInterviewAppointmentRepresentation interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public CommentInterviewInstructionRepresentation getInterviewInstruction() {
        return interviewInstruction;
    }

    public void setInterviewInstruction(CommentInterviewInstructionRepresentation interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public CommentPositionDetailRepresentation getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(CommentPositionDetailRepresentation positionDetail) {
        this.positionDetail = positionDetail;
    }

    public CommentOfferDetailRepresentation getOfferDetail() {
        return offerDetail;
    }

    public void setOfferDetail(CommentOfferDetailRepresentation offerDetail) {
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

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReasonSystem() {
        return rejectionReasonSystem;
    }

    public void setRejectionReasonSystem(String rejectionReasonSystem) {
        this.rejectionReasonSystem = rejectionReasonSystem;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
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

    public List<CommentCompetenceRepresentation> getCompetences() {
        return competences;
    }

    public void setCompetences(List<CommentCompetenceRepresentation> competences) {
        this.competences = competences;
    }

    public List<CommentAppointmentTimeslotRepresentation> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<LocalDateTime> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<LocalDateTime> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public List<DocumentRepresentation> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentRepresentation> documents) {
        this.documents = documents;
    }

    public CommentRepresentation withId(Integer id) {
        this.id = id;
        return this;
    }

    public CommentRepresentation withUser(UserRepresentationSimple user) {
        this.user = user;
        return this;
    }

    public CommentRepresentation withDelegateUser(UserRepresentationSimple delegateUser) {
        this.delegateUser = delegateUser;
        return this;
    }

    public CommentRepresentation withAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public CommentRepresentation withDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
        return this;
    }

    public CommentRepresentation withContent(String content) {
        this.content = content;
        return this;
    }

    public CommentRepresentation withState(PrismState state) {
        this.state = state;
        return this;
    }

    public CommentRepresentation withTransitionState(PrismState transitionState) {
        this.transitionState = transitionState;
        return this;
    }

    public CommentRepresentation withRating(BigDecimal rating) {
        this.rating = rating;
        return this;
    }

    public CommentRepresentation withApplicationIdentified(Boolean applicationIdentification) {
        this.applicationIdentified = applicationIdentification;
        return this;
    }

    public CommentRepresentation withApplicationEligible(PrismYesNoUnsureResponse applicationEligible) {
        this.applicationEligible = applicationEligible;
        return this;
    }

    public CommentRepresentation withApplicationInterested(Boolean applicationInterested) {
        this.applicationInterested = applicationInterested;
        return this;
    }

    public CommentRepresentation withInterviewAppointment(CommentInterviewAppointmentRepresentation interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
        return this;
    }

    public CommentRepresentation withInterviewInstruction(CommentInterviewInstructionRepresentation interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
        return this;
    }

    public CommentRepresentation withPositionDetail(CommentPositionDetailRepresentation positionDetail) {
        this.positionDetail = positionDetail;
        return this;
    }

    public CommentRepresentation withOfferDetail(CommentOfferDetailRepresentation offerDetail) {
        this.offerDetail = offerDetail;
        return this;
    }

    public CommentRepresentation withRecruiterAcceptAppointment(Boolean recruiterAcceptAppointment) {
        this.recruiterAcceptAppointment = recruiterAcceptAppointment;
        return this;
    }

    public CommentRepresentation withApplicationReserveStatus(PrismApplicationReserveStatus applicationReserveStatus) {
        this.applicationReserveStatus = applicationReserveStatus;
        return this;
    }

    public CommentRepresentation withRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public CommentRepresentation withRejectionReasonSystem(String rejectionReasonSystem) {
        this.rejectionReasonSystem = rejectionReasonSystem;
        return this;
    }

    public CommentRepresentation withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public CommentRepresentation withCompetences(List<CommentCompetenceRepresentation> competences) {
        this.competences = competences;
        return this;
    }

    public CommentRepresentation withAppointmentTimeslots(List<CommentAppointmentTimeslotRepresentation> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
        return this;
    }

    public CommentRepresentation withAppointmentPreferences(List<LocalDateTime> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
        return this;
    }

    public CommentRepresentation withDocuments(List<DocumentRepresentation> documents) {
        this.documents = documents;
        return this;
    }

}
