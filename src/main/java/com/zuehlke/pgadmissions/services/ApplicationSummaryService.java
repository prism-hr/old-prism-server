package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationSummaryDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.Comment;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.services.helpers.IntrospectionHelper;
import com.zuehlke.pgadmissions.services.helpers.SummaryHelper;

@Service
@Transactional
public class ApplicationSummaryService {

    @Autowired
    private ApplicationSummaryDAO applicationSummaryDAO;

    @Autowired
    private EntityService entityService;

    public void summariseApplication(Application application, Comment comment) {
        Integer currentRatingCount = application.getRatingCount();
        application.setRatingCount(SummaryHelper.incrementRunningCount(currentRatingCount));
        application.setRatingAverage(SummaryHelper.computeRunningAverage(currentRatingCount, application.getRatingAverage(), comment.getApplicationRating()));

        for (ParentResource parentResource : application.getParentResources()) {
            updateApplicationSummary(parentResource, application);
        }
    }

    public void summariseApplicationProcessing(Application application) throws DeduplicationException {
        StateGroup stateGroup = application.getState().getStateGroup();
        StateGroup previousStateGroup = application.getPreviousState().getStateGroup();

        LocalDate baseline = new LocalDate();
        ApplicationProcessing processing = createOrUpdateApplicationProcessing(application, stateGroup, baseline);
        createOrUpdateApplicationProcessingSummary(application, processing, stateGroup);

        Integer stateDuration = updatePreviousApplicationProcessing(application, previousStateGroup, baseline);
        updatePreviousApplicationProcessingSummary(application, previousStateGroup, stateDuration);
    }

    public void incrementApplicationCreatedCount(Application application) {
        incrementApplicationEventCount(application, "applicationCreatedCount");
    }

    public void incrementApplicationSubmittedCount(Application application) {
        incrementApplicationEventCount(application, "applicationSubmittedCount");
    }

    public void incrementApplicationApprovedCount(Application application) {
        incrementApplicationEventCount(application, "applicationApprovedCount");
    }

    public void incrementApplicationRejectedCount(Application application) {
        incrementApplicationEventCount(application, "applicationRejectedCount");
    }

    public void incrementApplicationWithdrawnCount(Application application) {
        incrementApplicationEventCount(application, "applicationWithdrawnCount");
    }

    private void updateApplicationSummary(ParentResource parentResource, Application application) {
        Integer currentRatingCountSum = parentResource.getApplicationRatingCount();
        parentResource.setApplicationRatingCount(SummaryHelper.incrementRunningCount(currentRatingCountSum));
        parentResource.setApplicationRatingCountAverageNonZero(SummaryHelper.computeRunningAverage(currentRatingCountSum,
                parentResource.getApplicationRatingCountAverageNonZero(), application.getRatingCount()));
        parentResource.setApplicationRatingAverage(SummaryHelper.computeRunningAverage(currentRatingCountSum, parentResource.getApplicationRatingAverage(),
                application.getRatingAverage()));
    }

    private ApplicationProcessing createOrUpdateApplicationProcessing(Application application, StateGroup stateGroup, LocalDate baseline)
            throws DeduplicationException {
        ApplicationProcessing transientProcessing = new ApplicationProcessing().withApplication(application).withStateGroup(stateGroup);
        ApplicationProcessing persistentProcessing = entityService.getDuplicateEntity(transientProcessing);

        if (persistentProcessing == null) {
            transientProcessing.setInstanceCount(1);
            transientProcessing.setLastUpdatedDate(baseline);
            persistentProcessing = (ApplicationProcessing) entityService.save(transientProcessing);
        } else {
            persistentProcessing.setInstanceCount(SummaryHelper.incrementRunningCount(persistentProcessing.getInstanceCount()));
            persistentProcessing.setLastUpdatedDate(baseline);
        }

        return persistentProcessing;
    }

    private Integer updatePreviousApplicationProcessing(Application application, StateGroup previousStateGroup, LocalDate baseline) {
        ApplicationProcessing previousProcessing = applicationSummaryDAO.getProcessing(application, previousStateGroup);
        Integer stateDuration = Days.daysBetween(previousProcessing.getLastUpdatedDate(), baseline).getDays();
        
        previousProcessing.setDayDurationAverage(SummaryHelper.computeRunningAverage((previousProcessing.getInstanceCount() - 1),
                previousProcessing.getDayDurationAverage(), stateDuration));
        previousProcessing.setLastUpdatedDate(baseline);
        
        return stateDuration;
    }

    private void createOrUpdateApplicationProcessingSummary(Application application, ApplicationProcessing processing, StateGroup stateGroup)
            throws DeduplicationException {
        for (ParentResource parentResource : application.getParentResources()) {
            ApplicationProcessingSummary transientSummary = new ApplicationProcessingSummary().withResource(parentResource).withStateGroup(stateGroup);
            ApplicationProcessingSummary persistentSummary = entityService.getDuplicateEntity(transientSummary);

            if (persistentSummary == null) {
                transientSummary.setInstanceCount(1);
                transientSummary.setInstanceCountLive(1);
                transientSummary.setInstanceCountAverageNonZero(new BigDecimal(1.00));
                entityService.save(transientSummary);
            } else {
                persistentSummary.setInstanceCount(SummaryHelper.incrementRunningCount(persistentSummary.getInstanceCount()));
                persistentSummary.setInstanceCountLive(SummaryHelper.incrementRunningCount(persistentSummary.getInstanceCountLive()));
                persistentSummary.setInstanceCountAverageNonZero(SummaryHelper.computeRunningAverage(persistentSummary.getInstanceCount(),
                        persistentSummary.getInstanceCountAverageNonZero(), 1));
            }
        }
    }

    private void updatePreviousApplicationProcessingSummary(Application application, StateGroup previousStateGroup, Integer stateDuration) {
        for (ParentResource parentResource : application.getParentResources()) {
            ApplicationProcessingSummary summary = applicationSummaryDAO.getProcessingSummary(parentResource, previousStateGroup);
            summary.setInstanceCountLive(SummaryHelper.decrementRunningCount(summary.getInstanceCountLive()));
            summary.setDayDurationAverage(SummaryHelper.computeRunningAverage(summary.getInstanceCount(), summary.getDayDurationAverage(), stateDuration));
        }
    }

    private void incrementApplicationEventCount(Application application, String eventCountProperty) {
        for (ParentResource parentResource : application.getParentResources()) {
            ParentResource parent = (ParentResource) IntrospectionHelper.getProperty(application, parentResource.getResourceScope().getLowerCaseName());
            Integer currentCount = (Integer) IntrospectionHelper.getProperty(parent, eventCountProperty);
            IntrospectionHelper.setProperty(parent, eventCountProperty, SummaryHelper.incrementRunningCount(currentCount));
        }
    }

}
