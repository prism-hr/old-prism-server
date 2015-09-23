package com.zuehlke.pgadmissions.services;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.dao.PrismDAO;
import com.zuehlke.pgadmissions.domain.AgeRange;
import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismDisplayPropertyDefinition;
import com.zuehlke.pgadmissions.domain.definitions.PrismDomicile;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.workflow.OpportunityType;

@Service
@Transactional
public class PrismService {

    @Inject
    private PrismDAO prismDAO;

    @Inject
    private EntityService entityService;

    public OpportunityType getOpportunityTypeById(PrismOpportunityType prismOpportunityType) {
        return entityService.getById(OpportunityType.class, prismOpportunityType);
    }

    public Domicile getDomicileById(PrismDomicile prismDomicile) {
        return entityService.getById(Domicile.class, prismDomicile);
    }

    public PrismDomicile getDomicileByName(String name) {
        PrismDisplayPropertyDefinition displayProperty = prismDAO.getDomicileDisplayPropertyByName(name);
        return displayProperty == null ? null : PrismDomicile.valueOf(displayProperty.name().replace("SYSTEM_DOMICILE_", ""));
    }

    public AgeRange getAgeRangeFromAge(Integer age) {
        return prismDAO.getAgeRange(age);
    }

}
