package com.zuehlke.pgadmissions.domain.builders;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;
import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;

public class ApplicationFormBuilder {

    private State status;
    private ProgramDetails programmeDetails;
    private PersonalDetails personalDetails;
    private ApplicationAddress applicationFormAddress;
    private Integer id;
    private User applicant;
    private Advert advert;
    private Date createdTimestamp;
    private Date submittedDate;
    private Date closingDate;
    private Date dueDate;
    private Boolean acceptedTerms;
    private List<Qualification> qualifications = new ArrayList<Qualification>();
    private List<Referee> referees = new ArrayList<Referee>();
    private List<EmploymentPosition> employmentPositions = new ArrayList<EmploymentPosition>();
    private List<Comment> comments = new ArrayList<Comment>();
    private List<Funding> fundings = new ArrayList<Funding>();
    private ApplicationDocument applicationFormDocument;
    private AdditionalInformation info;
    private Date rejectNotificationDate;
    private Rejection rejection;
    private String applicationNumber;
    private String uclBookingReferenceNumber;
    private String ipAddress;
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

    public ApplicationFormBuilder rejection(Rejection rejection) {
        this.rejection = rejection;
        return this;
    }

    public ApplicationFormBuilder rejectNotificationDate(Date rejectNotificationDate) {
        this.rejectNotificationDate = rejectNotificationDate;
        return this;
    }

    public ApplicationFormBuilder status(State status) {
        this.status = status;
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

    public ApplicationFormBuilder applicationFormAddress(ApplicationAddress applicationFormAddress) {
        this.applicationFormAddress = applicationFormAddress;
        return this;
    }

    public ApplicationFormBuilder applicationFormDocument(ApplicationDocument applicationFormDocument) {
        this.applicationFormDocument = applicationFormDocument;
        return this;
    }

    public ApplicationFormBuilder applicant(User applicant) {
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

    public ApplicationFormBuilder closingDate(Date closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ApplicationFormBuilder createdTimestamp(Date date) {
        this.createdTimestamp = date;
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

    public ApplicationFormBuilder useCustomReferenceQuestions(Boolean useCustomReferenceQuestions) {
        this.useCustomReferenceQuestions = useCustomReferenceQuestions;
        return this;
    }

    public ApplicationForm build() {
        ApplicationForm application = new ApplicationForm();
        application.setId(id);
        application.setApplicant(applicant);
        application.setSubmittedTimestamp(submittedDate);
        application.getReferees().addAll(referees);
        application.setCreatedTimestamp(createdTimestamp);
        application.getQualifications().addAll(qualifications);
        application.setProgramDetails(programmeDetails);
        application.getFundings().addAll(fundings);
        application.setApplicationAddress(applicationFormAddress);
        application.setApplicationDocument(applicationFormDocument);
        application.setPersonalDetails(personalDetails);
        application.setDueDate(dueDate);
        application.setAdvert(advert);
        application.setState(status);
        application.setAdditionalInformation(info);
        application.setAcceptedTermsOnSubmission(acceptedTerms);
        application.getApplicationComments().addAll(comments);
        application.setRejection(rejection);
        application.setApplicationNumber(applicationNumber);
        application.setClosingDate(closingDate);
        application.setRejectNotificationDate(rejectNotificationDate);
        application.setUclBookingReferenceNumber(uclBookingReferenceNumber);
        application.getEmploymentPositions().addAll(employmentPositions);
        application.setUseCustomReferenceQuestions(useCustomReferenceQuestions);

        try {
            application.setIpAddressAsString(ipAddress);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("There was an error setting the ip address");
        }

        return application;
    }
}
