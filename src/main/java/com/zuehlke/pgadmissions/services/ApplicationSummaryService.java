package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.ApplicationFormDocument;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ApplicationDescriptor;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
public class ApplicationSummaryService {

    private static final String NONE_PROVIDED = "None provided";

    private static final String DATE_FORMAT = "dd MMM yyyy";

    private final ApplicationFormService applicationsService;

    private final UserService userService;

    private final EncryptionHelper encryptionHelper;

    private final ActionService actionService;

    public ApplicationSummaryService() {
        this(null, null, null, null);
    }

    @Autowired
    public ApplicationSummaryService(final ApplicationFormService applicationsService, final UserService userService, final EncryptionHelper encryptionHelper,
            final ActionService actionService) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.encryptionHelper = encryptionHelper;
        this.actionService = actionService;
    }

    private void addApplicationProperties(final ApplicationForm form, final Map<String, String> result) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        result.put("applicationSubmissionDate", dateFormat.format(form.getSubmittedDate()));
        result.put("applicationUpdateDate", dateFormat.format(form.getLastUpdated()));
        ApplicationDescriptor applicationDescriptor = applicationsService.getApplicationDescriptorForUser(form, userService.getCurrentUser());
        result.put("requiresAttention", BooleanUtils.toStringTrueFalse(applicationDescriptor.getNeedsToSeeUrgentFlag()));
        result.put("applicationNumber", form.getApplicationNumber());
    }

    private void addActiveApplications(final RegisteredUser applicant, final Map<String, String> result) {
        result.put("numberOfActiveApplications", userService.getNumberOfActiveApplicationsForApplicant(applicant).toString());
    }

    private void addApplicantDetails(final ApplicationForm form, final Map<String, String> result) {
        result.put("title", form.getPersonalDetails() == null   ? "" : form.getPersonalDetails().getTitle().getDisplayValue());
        result.put("name", form.getApplicant().getDisplayName());
        result.put("phoneNumber", form.getPersonalDetails() == null   ? "" : form.getPersonalDetails().getPhoneNumber());
        result.put("email", form.getApplicant().getEmail());
        result.put("applicationStatus", form.getStatus().getId().displayValue());
    }

    private void addQualifications(final ApplicationForm form, final Map<String, String> result) {
        List<Qualification> qualifications = form.getQualifications();
        if (qualifications.isEmpty()) {
            result.put("mostRecentQualification", NONE_PROVIDED);
            return;
        }

        Qualification mostRecentQualification = Collections.max(qualifications, new Comparator<Qualification>() {
            @Override
            public int compare(Qualification o1, Qualification o2) {
                return o1.getQualificationAwardDate().compareTo(o2.getQualificationAwardDate());
            }
        });

        String title = mostRecentQualification.getQualificationTitle();
        String subject = mostRecentQualification.getQualificationSubject();
        String grade = mostRecentQualification.getQualificationGrade();
        String institution = mostRecentQualification.getInstitution().getName();

        StringBuilder builder = new StringBuilder();
        trimToEmptyAndJoin(builder, title, false);
        trimToEmptyAndJoin(builder, subject, false);
        trimToEmptyAndJoin(builder, grade, false);
        trimToEmptyAndJoin(builder, institution, true);
        result.put("mostRecentQualification", builder.toString());
    }

    private void trimToEmptyAndJoin(StringBuilder builder, String input, boolean addBracket) {
        String separator = " ";
        if (input != null) {
            if (addBracket) {
                builder.append("(" + input + ")");
                builder.append(separator);
            } else {
                builder.append(input);
                builder.append(separator);
            }
        }
    }

    private void addEmployments(final ApplicationForm form, Map<String, String> result) {
        List<EmploymentPosition> employments = form.getEmploymentPositions();
        if (employments.isEmpty()) {
            result.put("mostRecentEmployment", NONE_PROVIDED);
            return;
        }

        EmploymentPosition mostRecentEmployment = Collections.max(employments, new Comparator<EmploymentPosition>() {
            @Override
            public int compare(EmploymentPosition o1, EmploymentPosition o2) {
                Date e1Date = o1.getEndDate();
                Date e2Date = o2.getEndDate();
                if (e1Date == null) {
                    return -1;
                }
                if (e2Date == null) {
                    return 1;
                }
                return e1Date.compareTo(e2Date);
            }
        });
        result.put("mostRecentEmployment", mostRecentEmployment.getEmployerName());
    }

    private void addFundings(final ApplicationForm form, Map<String, String> result, final Gson gson) {
        Integer fundingSum = 0;
        for (Funding funding : form.getFundings()) {
                fundingSum = fundingSum + funding.getValueAsInteger();
        }
        result.put("fundingRequirements", fundingSum.toString());
    }

    private void addSkype(final ApplicationForm form, Map<String, String> result) {
        String skype;
        if(form.getPersonalDetails() == null || Strings.isNullOrEmpty(form.getPersonalDetails().getMessenger())){
            skype = "Not Provided";
        } else {
            skype = form.getPersonalDetails().getMessenger();
        }
        result.put("skype", skype);
    }

    private void addReferences(ApplicationForm form, Map<String, String> result) {
        Integer numberOfResponsed = CollectionUtils.countMatches(form.getReferees(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Referee) object).hasResponded();
            }
        });
        result.put("numberOfReferences", numberOfResponsed.toString());
    }

    private void addPersonalStatement(ApplicationForm form, Map<String, String> result) {
        ApplicationFormDocument applicationFormDocument = form.getApplicationFormDocument();
        if (applicationFormDocument == null) {
            result.put("personalStatementProvided", "false");
            result.put("cvProvided", "false");
        } else {
            Document personalStatement = applicationFormDocument.getPersonalStatement();
            if (personalStatement != null) {
                result.put("personalStatementProvided", "true");
                result.put("personalStatementId", encryptionHelper.encrypt(personalStatement.getId()));
                result.put("personalStatementFilename", personalStatement.getFileName());
            } else {
                result.put("personalStatementProvided", "false");
            }
    
            Document cv = applicationFormDocument.getCv();
            if (cv != null) {
                result.put("cvProvided", "true");
                result.put("cvId", encryptionHelper.encrypt(cv.getId()));
                result.put("cvFilename", cv.getFileName());
            } else {
                result.put("cvProvided", "false");
            }
        }
    }

    public Map<String, String> getSummary(final String applicationNumber) {
        ApplicationForm form = applicationsService.getByApplicationNumber(applicationNumber);

        if (form.getStatus().equals(ApplicationFormStatus.WITHDRAWN) || form.getStatus().equals(ApplicationFormStatus.UNSUBMITTED)) {
            return Collections.emptyMap();
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String> applicantResult = new HashMap<String, String>();

        addApplicationProperties(form, result);
        addActiveApplications(form.getApplicant(), result);
        addApplicantDetails(form, applicantResult);
        addQualifications(form, applicantResult);
        addEmployments(form, applicantResult);
        addFundings(form, applicantResult, gson);
        addReferences(form, result);
        addPersonalStatement(form, result);
        addSkype(form, applicantResult);
        result.put("applicant", gson.toJson(applicantResult));
        return result;
    }

}