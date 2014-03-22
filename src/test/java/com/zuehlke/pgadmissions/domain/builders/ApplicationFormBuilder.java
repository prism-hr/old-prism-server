package com.zuehlke.pgadmissions.domain.builders;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public class ApplicationFormBuilder {

    private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;
    private ProgramDetails programmeDetails;
    private PersonalDetails personalDetails;
    private Address currentAddress;
    private Address contactAddress;
    private Integer id;
    private RegisteredUser applicant;
    private String projectTitle;
    private Advert advert;
    private Date appDate;
    private Date submittedDate;
    private Date batchDeadline;
    private Date dueDate;
    private Boolean acceptedTerms;
    private List<Event> events = new ArrayList<Event>();
    private List<Qualification> qualifications = new ArrayList<Qualification>();
    private List<Referee> referees = new ArrayList<Referee>();
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();
    private List<Comment> comments = new ArrayList<Comment>();
    private List<Funding> fundings = new ArrayList<Funding>();
    private Document cv = null;
    private Document personalStatement = null;
    private AdditionalInformation info;
    private Date lastUpdated;
    private Date rejectNotificationDate;
    private List<ApprovalRound> approvalRounds = new ArrayList<ApprovalRound>();
    private List<Interview> interviews = new ArrayList<Interview>();
    private List<ReviewRound> reviewRounds = new ArrayList<ReviewRound>();
    private Interview latestInterview;
    private ReviewRound latestReviewRound;
    private ApprovalRound latestApprovalRound;
    private Rejection rejection;
    private RegisteredUser applicationAdministrator;
    private String applicationNumber;
    private String uclBookingReferenceNumber;
    private String ipAddress;
    private Boolean isEditableByApplicant = true;
    private BigDecimal averageRating;
    private Boolean useCustomReferenceQuestions = false;

    public ApplicationFormBuilder ipAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public ApplicationFormBuilder uclBookingReferenceNumber(String number) {
        this.uclBookingReferenceNumber = number;
        return this;
    }

    public ApplicationFormBuilder applicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
        return this;
    }

    public ApplicationFormBuilder applicationAdministrator(RegisteredUser applicationAdministrator) {
        this.applicationAdministrator = applicationAdministrator;
        return this;
    }

    public ApplicationFormBuilder rejection(Rejection rejection) {
        this.rejection = rejection;
        return this;
    }

    public ApplicationFormBuilder latestReviewRound(ReviewRound latestReviewRound) {
        this.latestReviewRound = latestReviewRound;
        return this;
    }

    public ApplicationFormBuilder latestInterview(Interview latestInterview) {
        this.latestInterview = latestInterview;
        return this;
    }

    public ApplicationFormBuilder latestApprovalRound(ApprovalRound latestApprovalRound) {
        this.latestApprovalRound = latestApprovalRound;
        return this;
    }

    public ApplicationFormBuilder reviewRounds(ReviewRound... reviewRounds) {
        for (ReviewRound reviewRound : reviewRounds) {
            this.reviewRounds.add(reviewRound);
        }
        return this;
    }

    public ApplicationFormBuilder approvalRounds(ApprovalRound... approvalRounds) {
        for (ApprovalRound approvalRound : approvalRounds) {
            this.approvalRounds.add(approvalRound);
        }
        return this;
    }

    public ApplicationFormBuilder lastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public ApplicationFormBuilder rejectNotificationDate(Date rejectNotificationDate) {
        this.rejectNotificationDate = rejectNotificationDate;
        return this;
    }

    public ApplicationFormBuilder interviews(Interview... interviews) {
        for (Interview interview : interviews) {
            this.interviews.add(interview);
        }
        return this;
    }

    public ApplicationFormBuilder status(ApplicationFormStatus status) {
        this.status = status;
        return this;
    }

    public ApplicationFormBuilder projectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
        return this;
    }

    public ApplicationFormBuilder acceptedTerms(Boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
        return this;
    }

    public ApplicationFormBuilder personalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
        return this;
    }

    public ApplicationFormBuilder programmeDetails(ProgramDetails programmeDetails) {
        this.programmeDetails = programmeDetails;
        return this;
    }

    public ApplicationFormBuilder contactAddress(Address contactAddress) {
        this.contactAddress = contactAddress;
        return this;
    }

    public ApplicationFormBuilder currentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
        return this;
    }

    public ApplicationFormBuilder personalStatement(Document personalStatement) {
        this.personalStatement = personalStatement;
        return this;
    }

    public ApplicationFormBuilder cv(Document cv) {
        this.cv = cv;
        return this;
    }

    public ApplicationFormBuilder applicant(RegisteredUser applicant) {
        this.applicant = applicant;
        return this;
    }

    public ApplicationFormBuilder advert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public ApplicationFormBuilder qualification(Qualification... qualifications) {
        for (Qualification qual : qualifications) {
            this.qualifications.add(qual);
        }
        return this;
    }

    public ApplicationFormBuilder events(Event... events) {
        for (Event event : events) {
            this.events.add(event);
        }
        return this;
    }

    public ApplicationFormBuilder comments(Comment... comments) {
        for (Comment comment : comments) {
            this.comments.add(comment);
        }
        return this;
    }

    public ApplicationFormBuilder qualifications(Qualification... qualifications) {
        for (Qualification qualification : qualifications) {
            this.qualifications.add(qualification);
        }
        return this;
    }

    public ApplicationFormBuilder referees(Referee... referees) {
        for (Referee referee : referees) {
            this.referees.add(referee);
        }
        return this;
    }

    public ApplicationFormBuilder employmentPosition(EmploymentPosition employmentPosition) {
        this.employmentPositions.add(employmentPosition);
        return this;
    }

    public ApplicationFormBuilder employmentPositions(EmploymentPosition... employmentPositions) {
        for (EmploymentPosition employmentPosition : employmentPositions) {
            this.employmentPositions.add(employmentPosition);
        }
        return this;
    }

    public ApplicationFormBuilder fundings(Funding... fundings) {
        for (Funding funding : fundings) {
            this.fundings.add(funding);
        }
        return this;
    }

    public ApplicationFormBuilder id(Integer id) {
        this.id = id;
        return this;
    }

    public ApplicationFormBuilder batchDeadline(Date batchDeadline) {
        this.batchDeadline = batchDeadline;
        return this;
    }

    public ApplicationFormBuilder appDate(Date date) {
        this.appDate = date;
        return this;
    }

    public ApplicationFormBuilder submittedDate(Date date) {
        this.submittedDate = date;
        return this;
    }

    public ApplicationFormBuilder dueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public ApplicationFormBuilder additionalInformation(AdditionalInformation info) {
        this.info = info;
        return this;
    }

    public ApplicationFormBuilder isEditableByApplicant(Boolean isEditableByApplicant) {
        this.isEditableByApplicant = isEditableByApplicant;
        return this;
    }

    public ApplicationFormBuilder averageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
        return this;
    }
    
    public ApplicationFormBuilder useCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
        return this;
    }

    public ApplicationForm build() {
        ApplicationForm application = new ApplicationForm();
        application.setId(id);
        application.setApplicant(applicant);
        application.setSubmittedDate(submittedDate);
        application.setReferees(referees);
        application.setApplicationTimestamp(appDate);
        application.getQualifications().addAll(qualifications);
        application.setProgramDetails(programmeDetails);
        application.getFundings().addAll(fundings);
        application.setCv(cv);
        application.setPersonalStatement(personalStatement);
        application.setContactAddress(contactAddress);
        application.setCurrentAddress(currentAddress);
        application.setPersonalDetails(personalDetails);
        application.setDueDate(dueDate);
        application.setAdvert(advert);
        application.setEvents(events);
        application.setProjectTitle(projectTitle);
        application.setStatus(status);
        application.setAdditionalInformation(info);
        application.setLastUpdated(lastUpdated);
        application.setAcceptedTermsOnSubmission(acceptedTerms);
        application.getApplicationComments().addAll(comments);
        application.getInterviews().addAll(interviews);
        application.getApprovalRounds().addAll(approvalRounds);
        application.setLatestInterview(latestInterview);
        application.setReviewRounds(reviewRounds);
        application.setLatestApprovalRound(latestApprovalRound);
        application.setLatestReviewRound(latestReviewRound);
        application.setRejection(rejection);
        application.setApplicationAdministrator(applicationAdministrator);
        application.setApplicationNumber(applicationNumber);
        application.setBatchDeadline(batchDeadline);
        application.setRejectNotificationDate(rejectNotificationDate);
        application.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        application.getEmploymentPositions().addAll(employmentPositions);
        application.setIsEditableByApplicant(isEditableByApplicant);
        application.setAverageRating(averageRating);
        application.setUseCustomReferenceQuestions(useCustomReferenceQuestions);

        try {
            application.setIpAddressAsString(ipAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("There was an error setting the ip address");
        }

        return application;
    }
}
