package com.zuehlke.pgadmissions.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.EmploymentPosition;
import com.zuehlke.pgadmissions.domain.Funding;
import com.zuehlke.pgadmissions.domain.Qualification;
import com.zuehlke.pgadmissions.domain.Referee;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;
import com.zuehlke.pgadmissions.dto.ApplicationActionsDefinition;
import com.zuehlke.pgadmissions.interceptors.EncryptionHelper;

@Service
public class ApplicationSummaryService {

    private static final String NONE_PROVIDED = "None provided";

    private static final String DATE_FORMAT = "dd MMM yyyy";
    
    private final ApplicationsService applicationsService;

    private final UserService userService;

    private final EncryptionHelper encryptionHelper;
    
    public ApplicationSummaryService() {
        this(null, null, null);
    }
    
    @Autowired
    public ApplicationSummaryService(final ApplicationsService applicationsService, final UserService userService, EncryptionHelper encryptionHelper) {
        this.applicationsService = applicationsService;
        this.userService = userService;
        this.encryptionHelper = encryptionHelper;
    }
    
    private void addApplicationProperties(final ApplicationForm form, final Map<String, String> result) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        result.put("applicationSubmissionDate", dateFormat.format(form.getSubmittedDate()));
        result.put("applicationUpdateDate", dateFormat.format(form.getLastUpdated()));
        ApplicationActionsDefinition actionsDefinition = applicationsService.getActionsDefinition(userService.getCurrentUser(), form);
        result.put("requiresAttention", BooleanUtils.toStringTrueFalse(actionsDefinition.isRequiresAttention()));
    }
    
    private void addActiveApplications(final RegisteredUser applicant, final Map<String, String> result) {
        result.put("numberOfActiveApplications", userService.getNumberOfActiveApplicationsForApplicant(applicant).toString());
    }
    
    private void addApplicantDetails(final ApplicationForm form, final Map<String, String> result) {
        result.put("title", form.getPersonalDetails().getTitle().getDisplayValue());
        result.put("name", form.getApplicant().getDisplayName());
        result.put("phoneNumber", form.getPersonalDetails().getPhoneNumber());
        result.put("email", form.getApplicant().getEmail());
        result.put("applicationStatus", form.getStatus().displayValue());
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

        String qulificationString = StringUtils.trimToEmpty(mostRecentQualification.getQualificationTitle())
                + StringUtils.trimToEmpty(mostRecentQualification.getQualificationSubject());
        result.put("mostRecentQualification", qulificationString);
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
        final List<Funding> fundings = form.getFundings();
        final ArrayList<String> descriptions = new ArrayList<String>(fundings.size());


        if (fundings.isEmpty()) {
            descriptions.add(NONE_PROVIDED);
        } 
        
        CollectionUtils.forAllDo(fundings, new Closure() {
            @Override
            public void execute(Object input) {
                Funding funding = (Funding) input;
                if (StringUtils.isNotBlank(funding.getDescription())) {
                    descriptions.add(funding.getDescription());
                }
            }
        });
        result.put("fundingRequirements", gson.toJson(descriptions));
    }
    
    private void addReferences(ApplicationForm form, Map<String, String> result) {
        Integer numberOfResponsed = CollectionUtils.countMatches(form.getReferees(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return  ((Referee) object).hasResponded();
            }
        });
        result.put("numberOfReferences", numberOfResponsed.toString());
    }
    
    private void addPersonalStatement(ApplicationForm form, Map<String, String> result) {
        Document personalStatement = form.getPersonalStatement();
        result.put("personalStatementId", encryptionHelper.encrypt(personalStatement.getId()));
        result.put("personalStatementFilename", personalStatement.getFileName());

        Document cv = form.getCv();
        if (cv != null) {
            result.put("cvProvided", "true");
            result.put("cvId", encryptionHelper.encrypt(cv.getId()));
            result.put("cvFilename", cv.getFileName());
        }
    }
    
    public Map<String, String> getSummary(final String applicationId) {
        ApplicationForm form = applicationsService.getApplicationByApplicationNumber(applicationId);

        if (form.getStatus().equals(ApplicationFormStatus.WITHDRAWN )|| form.getStatus().equals(ApplicationFormStatus.UNSUBMITTED)) {
            return Collections.emptyMap();
        }
        
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        Map<String , String> result = new HashMap<String, String>();
        Map<String, String> applicantResult = new HashMap<String, String>();
          
        addApplicationProperties(form, result);
        addActiveApplications(form.getApplicant(), result);
        addApplicantDetails(form, applicantResult);
        addQualifications(form, applicantResult);
        addEmployments(form, applicantResult);
        addFundings(form, applicantResult, gson);
        addReferences(form, result);
        addPersonalStatement(form, result);

        result.put("applicant", gson.toJson(applicantResult));
        return result;
    }

}
