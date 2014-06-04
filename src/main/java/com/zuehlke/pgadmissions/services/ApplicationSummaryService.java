package com.zuehlke.pgadmissions.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.enums.PrismState;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
public class ApplicationSummaryService {

    private static final String NONE_PROVIDED = "None provided";

    private static final String DATE_FORMAT = "dd MMM yyyy";

    @Autowired
    private ApplicationService applicationsService;

    @Autowired
    private UserService userService;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private CommentService commentService;

    private void addApplicationProperties(final Application application, final Map<String, String> result) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        DateTime updatedTimeStamp = commentService.getLastComment(application).getCreatedTimestamp();

        result.put("applicationSubmissionDate", dateFormat.format(application.getSubmittedTimestamp()));
        result.put("applicationUpdateDate", dateFormat.format(updatedTimeStamp));
        result.put("requiresAttention", BooleanUtils.toStringTrueFalse(application.isUrgent()));
        result.put("applicationNumber", application.getCode());
    }

    private void addActiveApplications(final User applicant, final Map<String, String> result) {
        result.put("numberOfActiveApplications", userService.getNumberOfActiveApplicationsForApplicant(applicant).toString());
    }

    private void addApplicantDetails(final Application form, final Map<String, String> result) {
        result.put("title", form.getPersonalDetails() == null ? "" : form.getPersonalDetails().getTitle().getDisplayValue());
        result.put("name", form.getUser().getDisplayName());
        result.put("phoneNumber", form.getPersonalDetails() == null ? "" : form.getPersonalDetails().getPhoneNumber());
        result.put("email", form.getUser().getEmail());
        result.put("applicationStatus", form.getState().getId().name());
    }

    private void addQualifications(final Application form, final Map<String, String> result) {
        List<ApplicationQualification> qualifications = form.getQualifications();
        if (qualifications.isEmpty()) {
            result.put("mostRecentQualification", NONE_PROVIDED);
            return;
        }

        ApplicationQualification mostRecentQualification = Collections.max(qualifications, new Comparator<ApplicationQualification>() {
            @Override
            public int compare(ApplicationQualification o1, ApplicationQualification o2) {
                return o1.getAwardDate().compareTo(o2.getAwardDate());
            }
        });

        String title = mostRecentQualification.getTitle();
        String subject = mostRecentQualification.getSubject();
        String grade = mostRecentQualification.getGrade();
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

    private void addEmployments(final Application form, Map<String, String> result) {
        // TODO implement query
        ApplicationEmploymentPosition recentEmployment = null;
        result.put("mostRecentEmployment", recentEmployment.getEmployerName());
    }

    private void addFundings(final Application form, Map<String, String> result, final Gson gson) {
        Integer fundingSum = 0;
        for (Funding funding : form.getFundings()) {
            fundingSum = fundingSum + funding.getValueAsInteger();
        }
        result.put("fundingRequirements", fundingSum.toString());
    }

    private void addSkype(final Application form, Map<String, String> result) {
        String skype;
        if (form.getPersonalDetails() == null || Strings.isNullOrEmpty(form.getPersonalDetails().getMessenger())) {
            skype = "Not Provided";
        } else {
            skype = form.getPersonalDetails().getMessenger();
        }
        result.put("skype", skype);
    }

    private void addReferences(Application form, Map<String, String> result) {
        Integer numberOfResponsed = CollectionUtils.countMatches(form.getApplicationReferees(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return ((Referee) object).getComment() != null;
            }
        });
        result.put("numberOfReferences", numberOfResponsed.toString());
    }

    private void addPersonalStatement(Application form, Map<String, String> result) {
        ApplicationDocument applicationFormDocument = form.getApplicationDocument();
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
        Application form = applicationsService.getByApplicationNumber(applicationNumber);

        if (form.getState().equals(PrismState.APPLICATION_WITHDRAWN) || form.getState().equals(PrismState.APPLICATION_UNSUBMITTED)) {
            return Collections.emptyMap();
        }

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String> applicantResult = new HashMap<String, String>();

        addApplicationProperties(form, result);
        addActiveApplications(form.getUser(), result);
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