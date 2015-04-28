package com.zuehlke.pgadmissions.workflow.resourcer.processors.preprocessors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.institution.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.services.InstitutionService;
import com.zuehlke.pgadmissions.workflow.resourcer.processors.ResourceProcessor;

@Component
public class ApplicationPreprocessor implements ResourceProcessor {

    @Inject
    private InstitutionService institutionService;

    @Override
    public void process(Resource resource, Comment comment) throws Exception {
        Application application = (Application) resource;
        if (comment.isApplicationCreatedComment()) {
            setReportingPeriod(application);
        }

        if (comment.isApplicationSubmittedComment()) {
            setSubmissionData(application);
        }
    }

    private void setReportingPeriod(Application application) {
        Institution institution = application.getInstitution();
        DateTime createdTimestamp = application.getCreatedTimestamp();
        Integer applicationYear = createdTimestamp.getYear();
        Integer applicationMonth = createdTimestamp.getMonthOfYear();
        application.setApplicationYear(institutionService.getBusinessYear(institution, applicationYear, applicationMonth));
        application.setApplicationMonth(applicationMonth);
        application.setApplicationMonthSequence(institutionService.getMonthOfBusinessYear(institution, applicationMonth));
    }

    private void setSubmissionData(Application application) {
        application.setSubmittedTimestamp(new DateTime());
        AdvertClosingDate advertClosingDate = application.getAdvert().getClosingDate();
        application.setClosingDate(advertClosingDate == null ? null : advertClosingDate.getClosingDate());
    }

}
