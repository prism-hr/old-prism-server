package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Address;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Event;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.NotificationRecord;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgrammeDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class ApplicationFormBuilder {

	private ApplicationFormStatus status = ApplicationFormStatus.UNSUBMITTED;
	private ProgrammeDetails programmeDetails;
	private PersonalDetails personalDetails;
	private Address currentAddress;
	private Address contactAddress;
	private Integer id;
	private RegisteredUser approver;
	private RegisteredUser applicant;
	private String projectTitle;
	private Program program;
	private Date appDate;
	private Date submittedDate;
	private Date dueDate;
	private CheckedStatus acceptedTerms;
	private List<NotificationRecord> notificationRecords = new ArrayList<NotificationRecord>();
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
	
	private List<ApprovalRound> approvalRounds = new ArrayList<ApprovalRound>();
	private List<Interview> interviews = new ArrayList<Interview>();
	private List<ReviewRound> reviewRounds = new ArrayList<ReviewRound>();
	private Interview latestInterview;
	private ReviewRound latestReviewRound;
	private ApprovalRound latestApprovalRound;
	private Rejection rejection;	
	private RegisteredUser applicationAdministrator;
	
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

	public ApplicationFormBuilder interviews(Interview...interviews) {
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

	public ApplicationFormBuilder acceptedTerms(CheckedStatus acceptedTerms) {
		this.acceptedTerms = acceptedTerms;
		return this;
	}
	
	public ApplicationFormBuilder personalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
		return this;
	}

	public ApplicationFormBuilder programmeDetails(ProgrammeDetails programmeDetails) {
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

	public ApplicationFormBuilder program(Program program) {
		this.program = program;
		return this;
	}

	public ApplicationFormBuilder approver(RegisteredUser user) {
		this.approver = user;
		return this;
	}

	public ApplicationFormBuilder qualification(Qualification qualification) {
		this.qualifications.add(qualification);
		return this;
	}

	public ApplicationFormBuilder notificationRecords(NotificationRecord... notificationRecords) {
		for (NotificationRecord notificationRecord : notificationRecords) {
			this.notificationRecords.add(notificationRecord);
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

	public ApplicationForm toApplicationForm() {
		ApplicationForm application = new ApplicationForm();
		application.setId(id);
		application.setApplicant(applicant);

		application.setSubmittedDate(submittedDate);

		application.setApprover(approver);
		application.setReferees(referees);
		
		application.setApplicationTimestamp(appDate);
		application.getQualifications().addAll(qualifications);
		application.setProgrammeDetails(programmeDetails);
		application.getFundings().addAll(fundings);
		application.setCv(cv);
		application.setPersonalStatement(personalStatement);
		application.setContactAddress(contactAddress);
		application.setCurrentAddress(currentAddress);
		application.setPersonalDetails(personalDetails);
		application.setDueDate(dueDate);

		application.setProgram(program);
		application.setEvents(events);
		application.setProjectTitle(projectTitle);
		application.setStatus(status);
		application.setAdditionalInformation(info);
		application.getNotificationRecords().addAll(notificationRecords);
		application.setLastUpdated(lastUpdated);
		application.setAcceptedTerms(acceptedTerms);
		application.getApplicationComments().addAll(comments);
		application.getInterviews().addAll(interviews);
		application.getApprovalRounds().addAll(approvalRounds);
		application.setLatestInterview(latestInterview);
		application.setReviewRounds(reviewRounds);
		application.setLatestApprovalRound(latestApprovalRound);
		application.setLatestReviewRound(latestReviewRound);
		application.setRejection(rejection);
		application.setApplicationAdministrator(applicationAdministrator);
		return application;
	}
}
