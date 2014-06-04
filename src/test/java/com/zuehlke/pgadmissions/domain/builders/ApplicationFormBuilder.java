package com.zuehlke.pgadmissions.domain.builders;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.domain.Program;
import com.zuehlke.pgadmissions.domain.ProgramDetails;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.State;
import com.zuehlke.pgadmissions.domain.User;

public class ApplicationFormBuilder {

    private State status;
    private ProgramDetails programmeDetails;
    private PersonalDetails personalDetails;
    private ApplicationAddress applicationFormAddress;
    private Integer id;
    private User applicant;
    private Program program;
    private DateTime createdTimestamp;
    private DateTime submittedDate;
    private LocalDate closingDate;
    private LocalDate dueDate;
    private Boolean acceptedTerms;
    private List<ApplicationQualification> qualifications = new ArrayList<ApplicationQualification>();
    private List<Referee> referees = new ArrayList<Referee>();
    private List<ApplicationEmploymentPosition> employmentPositions = new ArrayList<ApplicationEmploymentPosition>();
    private List<Funding> fundings = new ArrayList<Funding>();
    private ApplicationDocument applicationFormDocument;
    private ApplicationAdditionalInformation info;
    private String applicationNumber;

    public ApplicationFormBuilder applicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
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

    public ApplicationFormBuilder program(Program program) {
        this.program = program;
        return this;
    }

    public ApplicationFormBuilder qualification(ApplicationQualification... qualifications) {
        for (ApplicationQualification qual : qualifications) {
            this.qualifications.add(qual);
        }
        return this;
    }

    public ApplicationFormBuilder qualifications(ApplicationQualification... qualifications) {
        for (ApplicationQualification qualification : qualifications) {
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

    public ApplicationFormBuilder employmentPosition(ApplicationEmploymentPosition employmentPosition) {
        this.employmentPositions.add(employmentPosition);
        return this;
    }

    public ApplicationFormBuilder employmentPositions(ApplicationEmploymentPosition... employmentPositions) {
        for (ApplicationEmploymentPosition employmentPosition : employmentPositions) {
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

    public ApplicationFormBuilder closingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
        return this;
    }

    public ApplicationFormBuilder createdTimestamp(DateTime date) {
        this.createdTimestamp = date;
        return this;
    }

    public ApplicationFormBuilder submittedDate(DateTime date) {
        this.submittedDate = date;
        return this;
    }

    public ApplicationFormBuilder dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public ApplicationFormBuilder additionalInformation(ApplicationAdditionalInformation info) {
        this.info = info;
        return this;
    }

    public Application build() {
        Application application = new Application();
        application.setId(id);
        application.setUser(applicant);
        application.setProgram(program);
        application.setSubmittedTimestamp(submittedDate);
        application.getApplicationReferees().addAll(referees);
        application.setCreatedTimestamp(createdTimestamp);
        application.getQualifications().addAll(qualifications);
        application.setProgramDetails(programmeDetails);
        application.getFundings().addAll(fundings);
        application.setApplicationAddress(applicationFormAddress);
        application.setApplicationDocument(applicationFormDocument);
        application.setPersonalDetails(personalDetails);
        application.setDueDate(dueDate);
        application.setState(status);
        application.setAdditionalInformation(info);
        application.setAcceptedTerms(acceptedTerms);
        application.setCode(applicationNumber);
        application.setClosingDate(closingDate);
        application.getEmploymentPositions().addAll(employmentPositions);
        return application;
    }
}
