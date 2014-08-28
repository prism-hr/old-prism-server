package com.zuehlke.pgadmissions.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.zuehlke.pgadmissions.dao.ApplicationSummaryDAO;
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
    private ApplicationSummaryDAO applicationSummaryDAO;

    @Autowired
    private EntityService entityService;

    public void summariseApplication(Application application) {
        ApplicationRatingDTO ratingSummary = applicationSummaryDAO.getApplicationRatingSummary(application);
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
        updatePreviousApplicationProcessing(application, previousStateGroup, baseline);
    }

    private void updateApplicationSummary(Application application, PrismScope summaryScope) throws Exception {
        String summaryReference = summaryScope.getLowerCaseName();
        ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryReference);

        String[] summaryPropertiesRating = new String[] { "ratingCount", "ratingAverage" };

        for (String summaryProperty : summaryPropertiesRating) {
            Integer notNullValueCount = entityService.getNotNullValueCount(Application.class, summaryProperty,
                    ImmutableMap.of(summaryReference, (Object) summaryResource));

            for (Integer percentile : summaryPercentiles) {
                Integer valuesInSet = getActualPercentile(notNullValueCount, percentile);
                Object actualValue = applicationSummaryDAO.getPercentileValue(summaryResource, summaryProperty, valuesInSet);
                String propertyToSet = application.getResourceScope().getLowerCaseName() + WordUtils.capitalize(summaryProperty);
                summaryResource.setPercentileValue(propertyToSet, percentile, actualValue);
            }
        }
    }

    private void createOrUpdateApplicationProcessing(Application application, StateGroup stateGroup, LocalDate baseline) {
        ApplicationProcessing transientProcessing = new ApplicationProcessing().withApplication(application).withStateGroup(stateGroup);
        ApplicationProcessing persistentProcessing = entityService.getDuplicateEntity(transientProcessing);

        if (persistentProcessing == null) {
            createApplicationProcessing(transientProcessing, baseline);
            entityService.save(transientProcessing);
        } else {
            updateApplicationProcessing(baseline, persistentProcessing);
        }

        createOrUpdateApplicationProcessingSummary(application, stateGroup);
    }

    private void createApplicationProcessing(ApplicationProcessing transientProcessing, LocalDate baseline) {
        transientProcessing.setInstanceCount(1);
        transientProcessing.setDayDurationSum(0);
        transientProcessing.setLastUpdatedDate(baseline);
    }
    
    private void updateApplicationProcessing(LocalDate baseline, ApplicationProcessing persistentProcessing) {
        persistentProcessing.setInstanceCount(persistentProcessing.getInstanceCount() + 1);
        persistentProcessing.setLastUpdatedDate(baseline);
    }

    private void updatePreviousApplicationProcessing(Application application, StateGroup previousStateGroup, LocalDate baseline) {
        ApplicationProcessing persistentPreviousProcessing = applicationSummaryDAO.getProcessing(application, previousStateGroup);
        Integer actualStateDuration = Days.daysBetween(baseline, persistentPreviousProcessing.getLastUpdatedDate()).getDays();

        persistentPreviousProcessing.setDayDurationSum(persistentPreviousProcessing.getDayDurationSum() + actualStateDuration);
        persistentPreviousProcessing.setLastUpdatedDate(baseline);

        updatePreviousApplicationProcessingSummary(application, previousStateGroup);
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

    private void createApplicationProcessingSummary(ApplicationProcessingSummary transientSummary) {
        transientSummary.setInstanceSum(1);
        transientSummary.setInstanceSumLive(1);

        transientSummary.setInstanceCountAverage(new BigDecimal(1.00));
        
        transientSummary.setInstanceCount05(1);
        transientSummary.setInstanceCount20(1);
        transientSummary.setInstanceCount35(1);
        transientSummary.setInstanceCount50(1);
        transientSummary.setInstanceCount65(1);
        transientSummary.setInstanceCount80(1);
        transientSummary.setInstanceCount95(1);
        
        transientSummary.setDayDurationSumAverage(new BigDecimal(0.00));

        transientSummary.setDayDurationSum05(0);
        transientSummary.setDayDurationSum20(0);
        transientSummary.setDayDurationSum35(0);
        transientSummary.setDayDurationSum50(0);
        transientSummary.setDayDurationSum65(0);
        transientSummary.setDayDurationSum80(0);
        transientSummary.setDayDurationSum95(0);

        entityService.save(transientSummary);
    }

    private void updateApplicationProcessingSummary(ParentResource summaryResource, StateGroup stateGroup, ApplicationProcessingSummary persistentSummary) {
        BigDecimal instanceCountAverage = applicationSummaryDAO.getInstanceCountAverage(summaryResource, stateGroup);
        persistentSummary.setInstanceCountAverage(instanceCountAverage.setScale(2, RoundingMode.HALF_UP));
        
        persistentSummary.setInstanceSum(persistentSummary.getInstanceSum() + 1);
        persistentSummary.setInstanceSumLive(persistentSummary.getInstanceSumLive() + 1);

        updateApplicationProcessingSummaryPercentile(summaryResource, stateGroup, persistentSummary, "instanceCount");
    }

    private void updatePreviousApplicationProcessingSummary(Application application, StateGroup previousStateGroup) {
        for (PrismScope summaryScope : summaryScopes) {
            try {
                ParentResource summaryResource = (ParentResource) PropertyUtils.getSimpleProperty(application, summaryScope.getLowerCaseName());
                ApplicationProcessingSummary summary = applicationSummaryDAO.getProcessingSummary(summaryResource, previousStateGroup);
                
                BigDecimal instanceCountAverage = applicationSummaryDAO.getDayDurationSumAverage(summaryResource, previousStateGroup);
                summary.setDayDurationSumAverage(instanceCountAverage.setScale(2, RoundingMode.HALF_UP));
                
                summary.setInstanceSumLive(summary.getInstanceSumLive() - 1);
                updateApplicationProcessingSummaryPercentile(summaryResource, previousStateGroup, summary, "dayDurationSum");
            } catch (Exception e) {
                throw new Error(e);

            }
        }
    }

    private void updateApplicationProcessingSummaryPercentile(ParentResource summaryResource, StateGroup stateGroup,
            ApplicationProcessingSummary processingSummary, String property) {
        Integer valuesInSet = applicationSummaryDAO.getNotNullProcessingCount(summaryResource, stateGroup);

        for (Integer percentile : summaryPercentiles) {
            Integer actualPercentile = getActualPercentile(valuesInSet, percentile);
            Object actualValue = applicationSummaryDAO.getProcessingPercentileValue(summaryResource, stateGroup, property, actualPercentile);
            processingSummary.setPercentileValue(property, actualPercentile, actualValue);
        }
    }

    private int getActualPercentile(Integer valuesInSet, Integer percentile) {
        return new BigDecimal(percentile * (valuesInSet / 100.0)).setScale(0, RoundingMode.HALF_UP).intValue();
    }

}
