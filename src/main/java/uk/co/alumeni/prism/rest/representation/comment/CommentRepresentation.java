package uk.co.alumeni.prism.rest.representation.comment;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import uk.co.alumeni.prism.domain.definitions.PrismInterviewState;
import uk.co.alumeni.prism.domain.definitions.PrismRejectionReason;
import uk.co.alumeni.prism.domain.definitions.PrismYesNoUnsureResponse;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;

public class CommentRepresentation {

    private Integer id;

    private UserRepresentationSimple user;

    private UserRepresentationSimple delegateUser;

    private PrismAction action;

    private Boolean declinedResponse;

    private String content;

    private PrismState state;

    private PrismState transitionState;

    private Boolean shared;

    private Boolean onCourse;

    private PrismYesNoUnsureResponse eligible;

    private Boolean applicantKnown;

    private Integer applicantKnownDuration;

    private String applicantKnownCapacity;

    private BigDecimal rating;

    private Boolean interested;

    private PrismInterviewState interviewStatus;

    private CommentInterviewAppointmentRepresentation interviewAppointment;

    private CommentInterviewInstructionRepresentation interviewInstruction;

    private Boolean interviewAvailable;

    private CommentPositionDetailRepresentation positionDetail;

    private CommentOfferDetailRepresentation offerDetail;

    private Boolean recruiterAcceptAppointment;

    private Boolean partnerAcceptAppointment;

    private Boolean applicantAcceptAppointment;

    private PrismRejectionReason rejectionReason;

    private DateTime createdTimestamp;

    private DateTime submittedTimestamp;

    private List<CommentAssignedUserRepresentation> assignedUsers;

    private List<CommentCompetenceGroupRepresentation> competenceGroups;

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

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public Boolean getOnCourse() {
        return onCourse;
    }

    public void setOnCourse(Boolean onCourse) {
        this.onCourse = onCourse;
    }

    public PrismYesNoUnsureResponse getEligible() {
        return eligible;
    }

    public void setEligible(PrismYesNoUnsureResponse eligible) {
        this.eligible = eligible;
    }

    public Boolean getApplicantKnown() {
        return applicantKnown;
    }

    public void setApplicantKnown(Boolean applicantKnown) {
        this.applicantKnown = applicantKnown;
    }

    public Integer getApplicantKnownDuration() {
        return applicantKnownDuration;
    }

    public void setApplicantKnownDuration(Integer applicantKnownDuration) {
        this.applicantKnownDuration = applicantKnownDuration;
    }

    public String getApplicantKnownCapacity() {
        return applicantKnownCapacity;
    }

    public void setApplicantKnownCapacity(String applicantKnownCapacity) {
        this.applicantKnownCapacity = applicantKnownCapacity;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Boolean getInterested() {
        return interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
    }

    public PrismInterviewState getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(PrismInterviewState interviewStatus) {
        this.interviewStatus = interviewStatus;
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

    public Boolean getInterviewAvailable() {
        return interviewAvailable;
    }

    public void setInterviewAvailable(Boolean interviewAvailable) {
        this.interviewAvailable = interviewAvailable;
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

    public Boolean getPartnerAcceptAppointment() {
        return partnerAcceptAppointment;
    }

    public void setPartnerAcceptAppointment(Boolean partnerAcceptAppointment) {
        this.partnerAcceptAppointment = partnerAcceptAppointment;
    }

    public Boolean getApplicantAcceptAppointment() {
        return applicantAcceptAppointment;
    }

    public void setApplicantAcceptAppointment(Boolean applicantAcceptAppointment) {
        this.applicantAcceptAppointment = applicantAcceptAppointment;
    }

    public PrismRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(PrismRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getSubmittedTimestamp() {
        return submittedTimestamp;
    }

    public void setSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
    }

    public List<CommentAssignedUserRepresentation> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<CommentAssignedUserRepresentation> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<CommentCompetenceGroupRepresentation> getCompetenceGroups() {
        return competenceGroups;
    }

    public void setCompetenceGroups(List<CommentCompetenceGroupRepresentation> competenceGroups) {
        this.competenceGroups = competenceGroups;
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

    public CommentRepresentation withShared(Boolean shared) {
        this.shared = shared;
        return this;
    }

    public CommentRepresentation withOnCourse(Boolean onCourse) {
        this.onCourse = onCourse;
        return this;
    }

    public CommentRepresentation withEligible(PrismYesNoUnsureResponse eligible) {
        this.eligible = eligible;
        return this;
    }

    public CommentRepresentation withApplicantKnown(Boolean applicantKnown) {
        this.applicantKnown = applicantKnown;
        return this;
    }

    public CommentRepresentation withApplicantKnownDuration(Integer applicantKnownDuration) {
        this.applicantKnownDuration = applicantKnownDuration;
        return this;
    }

    public CommentRepresentation withApplicantKnownCapacity(String applicantKnownCapacity) {
        this.applicantKnownCapacity = applicantKnownCapacity;
        return this;
    }

    public CommentRepresentation withRating(BigDecimal rating) {
        this.rating = rating;
        return this;
    }

    public CommentRepresentation withInterested(Boolean interested) {
        this.interested = interested;
        return this;
    }

    public CommentRepresentation withInterviewState(PrismInterviewState interviewStatus) {
        this.interviewStatus = interviewStatus;
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

    public CommentRepresentation withInterviewAvailable(Boolean interviewAvailable) {
        this.interviewAvailable = interviewAvailable;
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

    public CommentRepresentation withPartnerAcceptAppointment(Boolean partnerAcceptAppointment) {
        this.partnerAcceptAppointment = partnerAcceptAppointment;
        return this;
    }

    public CommentRepresentation withApplicantAcceptAppointment(Boolean applicantAcceptAppointment) {
        this.applicantAcceptAppointment = applicantAcceptAppointment;
        return this;
    }

    public CommentRepresentation withRejectionReason(PrismRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public CommentRepresentation withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public CommentRepresentation withSubmittedTimestamp(DateTime submittedTimestamp) {
        this.submittedTimestamp = submittedTimestamp;
        return this;
    }

    public CommentRepresentation withCompetenceGroups(List<CommentCompetenceGroupRepresentation> competenceGroups) {
        this.competenceGroups = competenceGroups;
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
