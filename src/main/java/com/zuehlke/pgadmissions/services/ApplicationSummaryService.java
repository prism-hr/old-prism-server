package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ApplicationDAO;
import com.zuehlke.pgadmissions.domain.Application;
import com.zuehlke.pgadmissions.domain.ApplicationProcessing;
import com.zuehlke.pgadmissions.domain.ApplicationProcessingSummary;
import com.zuehlke.pgadmissions.domain.ParentResource;
import com.zuehlke.pgadmissions.domain.StateGroup;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.dto.ApplicationRatingDTO;

@Service
@Transactional
public class ApplicationSummaryService {

    private final Integer[] summaryPercentiles = new Integer[] { 5, 20, 35, 50, 65, 80, 95 };

    private final PrismScope[] summaryScopes = new PrismScope[] { PrismScope.PROJECT, PrismScope.PROGRAM, PrismScope.INSTITUTION };

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private EntityService entityService;

    public void summariseApplication(Application application) {
        ApplicationRatingDTO ratingSummary = applicationDAO.getApplicationRatingSummary(application);
        application.setRatingCount(ratingSummary.getRatingCount());
        application.setRatingAverage(ratingSummary.getRatingAverage().setScale(2, RoundingMode.HALF_UP));

        String[] summaryProperties = new String[] { "ratingCount", "ratingAverage" };

        for (PrismScope summaryScope : summaryScopes) {
            try {
                updateApplicationSummary(application, summaryScope, summaryProperties);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public void summariseApplicationProcessing(Application application) {
        StateGroup stateGroup = application.getState().getStateGroup();
        StateGroup previousStateGroup = application.getPreviousState().getStateGroup();

        LocalDate baseline = new LocalDate();
        createOrUpdateApplicationProcessing(application, stateGroup, previousStateGroup, baseline);
    }

    private void updateApplicationSummary(Application application, PrismScope summaryScope, String[] summaryProperties) throws Exception {
        String summaryReference = summaryScope.getLowerCaseName();
        ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryReference);

        for (String property : summaryProperties) {
            Integer notNullValueCount = entityService.getNotNullValueCount(Application.class, property,
                    ImmutableMap.of(summaryReference, (Object) summaryResource));

            for (Integer percentile : summaryPercentiles) {
                Integer valuesInSet = getActualPercentile(notNullValueCount, percentile);
                Object actualPercentileValue = applicationDAO.getPercentileValue(summaryResource, property, valuesInSet);
                summaryResource.setPercentileValue(application.getResourceScope().getLowerCaseName(), percentile, actualPercentileValue);
            }
        }
    }

    private void createOrUpdateApplicationProcessing(Application application, StateGroup stateGroup, StateGroup previousStateGroup, LocalDate baseline) {
        ApplicationProcessing transientCurrentProcessing = new ApplicationProcessing().withApplication(application).withStateGroup(stateGroup)
                .withInstanceCount(1).withDayDurationSum(0).withLastUpdateDate(baseline);

        ApplicationProcessing persistentCurrentProcessing = entityService.getDuplicateEntity(transientCurrentProcessing);

        if (persistentCurrentProcessing == null) {
            entityService.save(transientCurrentProcessing);
        } else {
            persistentCurrentProcessing.setInstanceCount(persistentCurrentProcessing.getInstanceCount() + 1);
            persistentCurrentProcessing.setLastUpdatedDate(baseline);
        }

        ApplicationProcessing persistentPreviousProcessing = applicationDAO.getApplicationProcessing(application, previousStateGroup);
        Integer actualStateDuration = Days.daysBetween(baseline, persistentPreviousProcessing.getLastUpdatedDate()).getDays();
        persistentPreviousProcessing.setDayDurationSum(persistentPreviousProcessing.getDayDurationSum() + actualStateDuration);
        persistentPreviousProcessing.setLastUpdatedDate(baseline);

        summariseApplicationProcessing(application, stateGroup, previousStateGroup);
    }

    private void summariseApplicationProcessing(Application application, StateGroup stateGroup, StateGroup previousStateGroup) {
        String[] properties = new String[] { "instanceCount", "dayDurationSum" };

        for (PrismScope summaryScope : summaryScopes) {
            try {
                createOrUpdateApplicationProcessingSummary(application, stateGroup, summaryScope, properties, ApplicationProcessingSummaryMode.CURRENT);
                createOrUpdateApplicationProcessingSummary(application, previousStateGroup, summaryScope, properties, ApplicationProcessingSummaryMode.PREVIOUS);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private void createOrUpdateApplicationProcessingSummary(Application application, StateGroup stateGroup, PrismScope summaryScope, String[] properties,
            ApplicationProcessingSummaryMode mode) throws Exception {
        ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryScope.getLowerCaseName());

        ApplicationProcessingSummary processingSummary = null;
        ApplicationProcessingSummary transientProcessingSummary = new ApplicationProcessingSummary().withResource(summaryResource).withStateGroup(stateGroup);
        ApplicationProcessingSummary persistentProcessingSummary = entityService.getDuplicateEntity(transientProcessingSummary);

        if (persistentProcessingSummary == null) {
            processingSummary = transientProcessingSummary;
        } else {
            processingSummary = persistentProcessingSummary;
        }

        if (mode == ApplicationProcessingSummaryMode.CURRENT) {
            processingSummary.setInstanceTotal(persistentProcessingSummary == null ? 1 : processingSummary.getInstanceTotal() + 1);
            processingSummary.setInstanceTotalLive(persistentProcessingSummary == null ? 1 : processingSummary.getInstanceTotalLive() + 1);
        } else {
            processingSummary.setInstanceTotalLive(processingSummary.getInstanceTotalLive() - 1);
        }

        for (String property : properties) {
            Integer valuesInSet = applicationDAO.getNotNullApplicationProcessingCount(summaryResource, stateGroup);

            for (Integer percentile : summaryPercentiles) {
                Integer actualPercentile = getActualPercentile(valuesInSet, percentile);
                Object actualPercentileValue = applicationDAO.getApplicationProcessingPercentileValue(summaryResource, stateGroup, property, actualPercentile);
                processingSummary.setPercentileValue(property, actualPercentile, actualPercentileValue);
            }
        }

        if (persistentProcessingSummary == null) {
            entityService.save(processingSummary);
        }
    }

    private int getActualPercentile(Integer valuesInSet, Integer percentile) {
        return new BigDecimal(percentile * (valuesInSet / 100.0)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private enum ApplicationProcessingSummaryMode {
        CURRENT, PREVIOUS;
    }

}