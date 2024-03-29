package uk.co.alumeni.prism.rest.dto.comment;

import org.joda.time.LocalDateTime;
import uk.co.alumeni.prism.domain.definitions.PrismInterviewState;
import uk.co.alumeni.prism.domain.definitions.PrismRejectionReason;
import uk.co.alumeni.prism.domain.definitions.PrismRoleContext;
import uk.co.alumeni.prism.domain.definitions.PrismYesNoUnsureResponse;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismState;
import uk.co.alumeni.prism.rest.dto.DocumentDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceCreationDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceRelationCreationDTO;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.CREATE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismActionCategory.MESSAGE_RESOURCE;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScopeCategory.ORGANIZATION;

public class CommentDTO {

    private Integer id;

    private Integer user;

    private Integer delegateUser;

    private PrismAction action;

    private PrismRoleContext roleContext;

    private Boolean declinedResponse;

    @Size(max = 50000)
    private String content;

    private PrismState transitionState;

    private Boolean shared;

    private Boolean onCourse;

    private PrismYesNoUnsureResponse eligible;

    private Boolean applicantKnown;

    private Integer applicantKnownDuration;

    private String applicantKnownCapacity;

    private BigDecimal rating;

    private Boolean interested;

    private PrismInterviewState interviewState;

    @Valid
    private CommentInterviewAppointmentDTO interviewAppointment;

    @Valid
    private CommentInterviewInstructionDTO interviewInstruction;

    private Boolean interviewAvailable;

    @Valid
    private CommentPositionDetailDTO positionDetail;

    @Valid
    private CommentOfferDetailDTO offerDetail;

    private Boolean recruiterAcceptAppointment;

    private Boolean partnerAcceptAppointment;

    private Boolean applicantAcceptAppointment;

    private PrismRejectionReason rejectionReason;

    @Valid
    private ResourceCreationDTO resource;

    @Valid
    private ResourceCreationDTO resourceInviting;

    @Valid
    private ResourceRelationCreationDTO resourceInvitation;

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
    private List<DocumentDTO> documents;

    private Boolean submit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public PrismRoleContext getRoleContext() {
        return roleContext;
    }

    public void setRoleContext(PrismRoleContext roleContext) {
        this.roleContext = roleContext;
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

    public PrismInterviewState getInterviewState() {
        return interviewState;
    }

    public void setInterviewState(PrismInterviewState interviewState) {
        this.interviewState = interviewState;
    }

    public CommentInterviewAppointmentDTO getInterviewAppointment() {
        return interviewAppointment;
    }

    public void setInterviewAppointment(CommentInterviewAppointmentDTO interviewAppointment) {
        this.interviewAppointment = interviewAppointment;
    }

    public CommentInterviewInstructionDTO getInterviewInstruction() {
        return interviewInstruction;
    }

    public void setInterviewInstruction(CommentInterviewInstructionDTO interviewInstruction) {
        this.interviewInstruction = interviewInstruction;
    }

    public Boolean getInterviewAvailable() {
        return interviewAvailable;
    }

    public void setInterviewAvailable(Boolean interviewAvailable) {
        this.interviewAvailable = interviewAvailable;
    }

    public CommentPositionDetailDTO getPositionDetail() {
        return positionDetail;
    }

    public void setPositionDetail(CommentPositionDetailDTO positionDetail) {
        this.positionDetail = positionDetail;
    }

    public CommentOfferDetailDTO getOfferDetail() {
        return offerDetail;
    }

    public void setOfferDetail(CommentOfferDetailDTO offerDetail) {
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

    public Boolean getDeclinedResponse() {
        return declinedResponse;
    }

    public void setDeclinedResponse(Boolean declinedResponse) {
        this.declinedResponse = declinedResponse;
    }

    public PrismRejectionReason getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(PrismRejectionReason rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ResourceCreationDTO getResource() {
        return resource;
    }

    public void setResource(ResourceCreationDTO resource) {
        this.resource = resource;
    }

    public ResourceCreationDTO getResourceInviting() {
        return resourceInviting;
    }

    public void setResourceInviting(ResourceCreationDTO resourceInviting) {
        this.resourceInviting = resourceInviting;
    }

    public ResourceRelationCreationDTO getResourceInvitation() {
        return resourceInvitation;
    }

    public void setResourceInvitation(ResourceRelationCreationDTO resourceInvitation) {
        this.resourceInvitation = resourceInvitation;
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

    public List<DocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentDTO> documents) {
        this.documents = documents;
    }

    public Boolean getSubmit() {
        return submit;
    }

    public void setSubmit(Boolean submit) {
        this.submit = submit;
    }

    public boolean isCreateComment() {
        return action.getActionCategory().equals(CREATE_RESOURCE);
    }

    public boolean isEnquirerAssignmentComment() {
        return action.getActionCategory().equals(MESSAGE_RESOURCE) && interested == true;
    }

    public boolean isCompleteComment() {
        return resource.getScope().getScopeCategory().equals(ORGANIZATION) && action.name().endsWith("_COMPLETE");
    }

    public boolean isBypassComment() {
        return !(roleContext == null && resourceInvitation == null);
    }

    public CommentDTO withUser(Integer user) {
        this.user = user;
        return this;
    }

    public CommentDTO withAction(PrismAction action) {
        this.action = action;
        return this;
    }

    public CommentDTO withResource(ResourceCreationDTO resource) {
        this.resource = resource;
        return this;
    }

}
