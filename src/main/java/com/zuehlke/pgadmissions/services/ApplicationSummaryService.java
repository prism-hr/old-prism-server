package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.ApplicationSummaryDAO;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.application.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.workflow.StateGroup;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.rest.representation.ApplicationSummaryRepresentation.ApplicationProcessingRepresentation;
import com.zuehlke.pgadmissions.rest.representation.ResourceSummaryRepresentation.ApplicationProcessingSummaryRepresentation;
import com.zuehlke.pgadmissions.utils.ReflectionUtils;
import com.zuehlke.pgadmissions.utils.SummaryUtils;

@Service
@Transactional
public class ApplicationSummaryService {

    @Autowired
    private ApplicationSummaryDAO applicationSummaryDAO;

    @Autowired
    private EntityService entityService;

    public void summariseApplication(Application application, Comment comment) {
        Integer currentRatingCount = application.getApplicationRatingCount();
        application.setApplicationRatingCount(SummaryUtils.incrementRunningCount(currentRatingCount));
        application.setApplicationRatingAverage(SummaryUtils.computeRunningAverage(currentRatingCount, application.getApplicationRatingAverage(),
                comment.getApplicationRating()));

        for (ResourceParent parentResource : application.getParentResources()) {
            updateApplicationSummary(parentResource, application);
        }
    }

    public void summariseApplicationProcessing(Application application) throws DeduplicationException {
        StateGroup stateGroup = application.getState().getStateGroup();
        StateGroup previousStateGroup = application.getPreviousState().getStateGroup();

        LocalDate baseline = new LocalDate();
        ApplicationProcessing processing = createOrUpdateApplicationProcessing(application, stateGroup, baseline);
        createOrUpdateApplicationProcessingSummary(application, processing, stateGroup);

        updatePreviousApplicationProcessing(application, previousStateGroup, baseline);
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

    public List<ApplicationProcessingRepresentation> getProcessings(Application application) {
        return applicationSummaryDAO.getProcessings(application);
    }

    public List<ApplicationProcessingSummaryRepresentation> getProcessingSummaries(ResourceParent resource) {
        return applicationSummaryDAO.getProcessingSummaries(resource);
    }

    private void updateApplicationSummary(ResourceParent parentResource, Application application) {
        Integer currentRatingCountSum = parentResource.getApplicationRatingCount();
        parentResource.setApplicationRatingCount(SummaryUtils.incrementRunningCount(currentRatingCountSum));
        parentResource.setApplicationRatingCountAverageNonZero(SummaryUtils.computeRunningAverage(currentRatingCountSum,
                parentResource.getApplicationRatingCountAverageNonZero(), application.getApplicationRatingCount()));
        parentResource.setApplicationRatingAverage(SummaryUtils.computeRunningAverage(currentRatingCountSum, parentResource.getApplicationRatingAverage(),
                application.getApplicationRatingAverage()));
    }

    private ApplicationProcessing createOrUpdateApplicationProcessing(Application application, StateGroup stateGroup, LocalDate baseline)
            throws DeduplicationException {
        ApplicationProcessing transientProcessing = new ApplicationProcessing().withApplication(application).withStateGroup(stateGroup);
        ApplicationProcessing persistentProcessing = entityService.getDuplicateEntity(transientProcessing);

        if (persistentProcessing == null) {
            transientProcessing.setInstanceCount(1);
            transientProcessing.setLastUpdatedDate(baseline);
            entityService.save(transientProcessing);
            return transientProcessing;
        } else {
            persistentProcessing.setInstanceCount(SummaryUtils.incrementRunningCount(persistentProcessing.getInstanceCount()));
            persistentProcessing.setLastUpdatedDate(baseline);
            return persistentProcessing;
        }
    }

    private void updatePreviousApplicationProcessing(Application application, StateGroup previousStateGroup, LocalDate baseline) {
        ApplicationProcessing previousProcessing = applicationSummaryDAO.getProcessing(application, previousStateGroup);

        if (previousProcessing == null) {
            return;
        }

        Integer stateDuration = Days.daysBetween(previousProcessing.getLastUpdatedDate(), baseline).getDays();

        previousProcessing.setDayDurationAverage(SummaryUtils.computeRunningAverage((previousProcessing.getInstanceCount() - 1),
                previousProcessing.getDayDurationAverage(), stateDuration));
        previousProcessing.setLastUpdatedDate(baseline);

        updatePreviousApplicationProcessingSummary(application, previousStateGroup, stateDuration);
    }

    private void createOrUpdateApplicationProcessingSummary(Application application, ApplicationProcessing processing, StateGroup stateGroup)
            throws DeduplicationException {
        for (ResourceParent parentResource : application.getParentResources()) {
            ApplicationProcessingSummary transientSummary = new ApplicationProcessingSummary().withResource(parentResource).withStateGroup(stateGroup);
            ApplicationProcessingSummary persistentSummary = entityService.getDuplicateEntity(transientSummary);

            if (persistentSummary == null) {
                transientSummary.setInstanceCount(1);
                transientSummary.setInstanceCountLive(1);
                transientSummary.setInstanceCountAverageNonZero(new BigDecimal(1.00));
                entityService.save(transientSummary);
            } else {
                persistentSummary.setInstanceCount(SummaryUtils.incrementRunningCount(persistentSummary.getInstanceCount()));
                persistentSummary.setInstanceCountLive(SummaryUtils.incrementRunningCount(persistentSummary.getInstanceCountLive()));
                persistentSummary.setInstanceCountAverageNonZero(SummaryUtils.computeRunningAverage(persistentSummary.getInstanceCount(),
                        persistentSummary.getInstanceCountAverageNonZero(), 1));
            }
        }
    }

    private void updatePreviousApplicationProcessingSummary(Application application, StateGroup previousStateGroup, Integer stateDuration) {
        for (ResourceParent parentResource : application.getParentResources()) {
            ApplicationProcessingSummary summary = applicationSummaryDAO.getProcessingSummary(parentResource, previousStateGroup);
            if (summary == null) {
                continue;
            }
            summary.setInstanceCountLive(SummaryUtils.decrementRunningCount(summary.getInstanceCountLive()));
            summary.setDayDurationAverage(SummaryUtils.computeRunningAverage(summary.getInstanceCount(), summary.getDayDurationAverage(), stateDuration));
        }
    }

    private void incrementApplicationEventCount(Application application, String eventCountProperty) {
        for (ResourceParent parentResource : application.getParentResources()) {
            ResourceParent parent = (ResourceParent) ReflectionUtils.getProperty(application, parentResource.getResourceScope().getLowerCamelName());
            Integer currentCount = (Integer) ReflectionUtils.getProperty(parent, eventCountProperty);
            ReflectionUtils.setProperty(parent, eventCountProperty, SummaryUtils.incrementRunningCount(currentCount));
        }
    }

}
