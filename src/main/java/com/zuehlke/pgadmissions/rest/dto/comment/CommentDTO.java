package com.zuehlke.pgadmissions.rest.dto.comment;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.joda.time.LocalDateTime;

import com.zuehlke.pgadmissions.domain.definitions.PrismApplicationReserveStatus;
import com.zuehlke.pgadmissions.domain.definitions.PrismYesNoUnsureResponse;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismAction;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.resource.ResourceCreationDTO;

public class CommentDTO {

    private Integer user;

    private Integer delegateUser;

    private PrismAction action;

    private Boolean applicationRetain;

    private Boolean applicationRecommend;

    @Size(max = 50000)
    private String content;

    private PrismState transitionState;

    private PrismYesNoUnsureResponse applicationEligible;

    private Boolean applicationInterested;

    private BigDecimal applicationRating;

    @Valid
    private CommentInterviewAppointmentDTO interviewAppointment;

    @Valid
    private CommentInterviewInstructionDTO interviewInstruction;

    @Valid
    private CommentPositionDetailDTO positionDetail;

    @Valid
    private CommentOfferDetailDTO offerDetail;

    private Boolean recruiterAcceptAppointment;

    private PrismApplicationReserveStatus applicationReserveStatus;

    private Boolean declinedResponse;

    private Integer rejectionReason;

    @Valid
    private ResourceCreationDTO resource;

    @Valid
    private List<CommentAssignedUserDTO> assignedUsers;

    @Valid
    private List<PrismState> secondaryTransitionStates;

    @Valid
    private List<CommentCompetenceDTO> competences;

    @Size(max = 200)
    private List<LocalDateTime> appointmentTimeslots;

    @Size(max = 200)
    private List<Integer> appointmentPreferences;

    @Valid
    private List<CommentCustomResponseDTO> customResponses;

    @Valid
    private List<FileDTO> documents;

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(Integer delegateUser) {
        this.delegateUser = delegateUser;
    }

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public final Boolean getApplicationRetain() {
        return applicationRetain;
    }

    public final void setApplicationRetain(Boolean applicationRetain) {
        this.applicationRetain = applicationRetain;
    }

    public final Boolean getApplicationRecommend() {
        return applicationRecommend;
    }

    public final void setApplicationRecommend(Boolean applicationRecommend) {
        this.applicationRecommend = applicationRecommend;
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

    public BigDecimal getApplicationRating() {
        return applicationRating;
    }

    public void setApplicationRating(BigDecimal applicationRating) {
        this.applicationRating = applicationRating;
    }

    public final CommentInterviewAppointmentDTO getInterviewAppointment() {
        return interviewAppointment;
    }

    public final void setInterviewAppointment(CommentInterviewAppointmentDTO interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public final CommentInterviewInstructionDTO getInterviewInstruction() {
        return interviewInstruction;
    }

    public final void setInterviewInstruction(CommentInterviewInstructionDTO interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public final CommentPositionDetailDTO getPositionDetail() {
        return positionDetail;
    }

    public final void setPositionDetail(CommentPositionDetailDTO positionDetail) {
        this.positionDetail = positionDetail;
    }

    public final CommentOfferDetailDTO getOfferDetail() {
        return offerDetail;
    }

    public final void setOfferDetail(CommentOfferDetailDTO offerDetail) {
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

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }

    public Integer getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(Integer rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ResourceCreationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceCreationDTO resource) {
        this.resource = resource;
    }

    public List<CommentAssignedUserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<CommentAssignedUserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public List<PrismState> getSecondaryTransitionStates() {
        return secondaryTransitionStates;
    }

    public void setSecondaryTransitionStates(List<PrismState> secondaryTransitionStates) {
        this.secondaryTransitionStates = secondaryTransitionStates;
    }

    public List<CommentCompetenceDTO> getCompetences() {
        return competences;
    }

    public void setCompetences(List<CommentCompetenceDTO> competences) {
        this.competences = competences;
    }

    public List<LocalDateTime> getAppointmentTimeslots() {
        return appointmentTimeslots;
    }

    public void setAppointmentTimeslots(List<LocalDateTime> appointmentTimeslots) {
        this.appointmentTimeslots = appointmentTimeslots;
    }

    public List<Integer> getAppointmentPreferences() {
        return appointmentPreferences;
    }

    public void setAppointmentPreferences(List<Integer> appointmentPreferences) {
        this.appointmentPreferences = appointmentPreferences;
    }

    public List<CommentCustomResponseDTO> getCustomResponses() {
        return customResponses;
    }

    public void setCustomResponses(List<CommentCustomResponseDTO> customResponses) {
        this.customResponses = customResponses;
    }

    public List<FileDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<FileDTO> documents) {
        this.documents = documents;
    }

}
