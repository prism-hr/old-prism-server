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
    
    private final String[] summaryPropertiesRating = new String[] { "ratingCount", "ratingAverage" };
    
    private final String[] summaryPropertiesProcessing = new String[] { "instanceCount", "dayDurationSum" };

    @Autowired
    private ApplicationDAO applicationDAO;

    @Autowired
    private EntityService entityService;

    public void summariseApplication(Application application) {
        ApplicationRatingDTO ratingSummary = applicationDAO.getApplicationRatingSummary(application);
        application.setRatingCount(ratingSummary.getRatingCount());
        application.setRatingAverage(ratingSummary.getRatingAverage().setScale(2, RoundingMode.HALF_UP));

        for (PrismScope summaryScope : summaryScopes) {
            try {
                updateApplicationSummary(application, summaryScope);
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public void summariseApplicationProcessing(Application application) {
        StateGroup stateGroup = application.getState().getStateGroup();
        StateGroup previousStateGroup = application.getPreviousState().getStateGroup();

        LocalDate baseline = new LocalDate();
        createOrUpdateApplicationProcessing(application, stateGroup, baseline);
        updateApplicationProcessing(application, previousStateGroup, baseline);
    }

    private void updateApplicationSummary(Application application, PrismScope summaryScope) throws Exception {
        String summaryReference = summaryScope.getLowerCaseName();
        ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryReference);

        for (String summaryProperty : summaryPropertiesRating) {
            Integer notNullValueCount = entityService.getNotNullValueCount(Application.class, summaryProperty,
                    ImmutableMap.of(summaryReference, (Object) summaryResource));

            for (Integer percentile : summaryPercentiles) {
                Integer valuesInSet = getActualPercentile(notNullValueCount, percentile);
                Object actualValue = applicationDAO.getPercentileValue(summaryResource, summaryProperty, valuesInSet);
                summaryResource.setPercentileValue(summaryProperty, percentile, actualValue);
            }
        }
    }

    private void createOrUpdateApplicationProcessing(Application application, StateGroup stateGroup, LocalDate baseline) {
        ApplicationProcessing transientCurrentProcessing = new ApplicationProcessing().withApplication(application).withStateGroup(stateGroup)
                .withInstanceCount(1).withDayDurationSum(0).withLastUpdateDate(baseline);

        ApplicationProcessing persistentCurrentProcessing = entityService.getDuplicateEntity(transientCurrentProcessing);

        if (persistentCurrentProcessing == null) {
            entityService.save(transientCurrentProcessing);
        } else {
            persistentCurrentProcessing.setInstanceCount(persistentCurrentProcessing.getInstanceCount() + 1);
            persistentCurrentProcessing.setLastUpdatedDate(baseline);
        }

        createOrUpdateApplicationProcessingSummary(application, stateGroup);
    }

    private void updateApplicationProcessing(Application application, StateGroup stateGroup, LocalDate baseline) {
        ApplicationProcessing persistentPreviousProcessing = applicationDAO.getProcessing(application, stateGroup);
        Integer actualStateDuration = Days.daysBetween(baseline, persistentPreviousProcessing.getLastUpdatedDate()).getDays();

        persistentPreviousProcessing.setDayDurationSum(persistentPreviousProcessing.getDayDurationSum() + actualStateDuration);
        persistentPreviousProcessing.setLastUpdatedDate(baseline);

        updateApplicationProcessingSummary(application, stateGroup);
    }

    private void createOrUpdateApplicationProcessingSummary(Application application, StateGroup stateGroup) {
        for (PrismScope summaryScope : summaryScopes) {
            try {
                ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryScope.getLowerCaseName());
                ApplicationProcessingSummary transientSummary = new ApplicationProcessingSummary().withResource(summaryResource).withStateGroup(stateGroup);
                ApplicationProcessingSummary persistentSummary = entityService.getDuplicateEntity(transientSummary);

                if (persistentSummary == null) {
                    createApplicationProcessingSummary(transientSummary);
                } else {
                    updateApplicationProcessingSummary(summaryResource, stateGroup, persistentSummary);
                }

            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private void updateApplicationProcessingSummary(ParentResource summaryResource, StateGroup stateGroup, ApplicationProcessingSummary persistentSummary) {
        persistentSummary.setInstanceTotal(persistentSummary.getInstanceTotal() + 1);
        persistentSummary.setInstanceTotalLive(persistentSummary.getInstanceTotalLive() + 1);

        for (String property : summaryPropertiesProcessing) {
            Integer valuesInSet = applicationDAO.getNotNullProcessingCount(summaryResource, stateGroup);

            for (Integer percentile : summaryPercentiles) {
                Integer actualPercentile = getActualPercentile(valuesInSet, percentile);
                Object actualValue = applicationDAO.getProcessingPercentileValue(summaryResource, stateGroup, property, actualPercentile);
                persistentSummary.setPercentileValue(property, actualPercentile, actualValue);
            }
        }
    }
    
    private void updateApplicationProcessingSummary(Application application, StateGroup stateGroup) {
        for (PrismScope summaryScope : summaryScopes) {
            try {
                ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryScope.getLowerCaseName());
                ApplicationProcessingSummary summary = applicationDAO.getProcessingSummary(summaryResource, stateGroup);

                summary.setInstanceTotalLive(summary.getInstanceTotalLive() - 1);

            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    private void createApplicationProcessingSummary(ApplicationProcessingSummary transientProcessingSummary) {
        transientProcessingSummary.setInstanceTotal(1);
        transientProcessingSummary.setInstanceTotalLive(1);

        transientProcessingSummary.setInstanceCountCount05(1);
        transientProcessingSummary.setInstanceCountCount20(1);
        transientProcessingSummary.setInstanceCountCount35(1);
        transientProcessingSummary.setInstanceCountCount50(1);
        transientProcessingSummary.setInstanceCountCount65(1);
        transientProcessingSummary.setInstanceCountCount80(1);
        transientProcessingSummary.setInstanceCountCount95(1);

        transientProcessingSummary.setDayDurationSum05(0);
        transientProcessingSummary.setDayDurationSum20(0);
        transientProcessingSummary.setDayDurationSum35(0);
        transientProcessingSummary.setDayDurationSum50(0);
        transientProcessingSummary.setDayDurationSum65(0);
        transientProcessingSummary.setDayDurationSum80(0);
        transientProcessingSummary.setDayDurationSum95(0);

        entityService.save(transientProcessingSummary);
    }

    private int getActualPercentile(Integer valuesInSet, Integer percentile) {
        return new BigDecimal(percentile * (valuesInSet / 100.0)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

}