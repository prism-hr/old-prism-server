package uk.co.alumeni.prism.services;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.dao.PrismDAO;
import uk.co.alumeni.prism.domain.AgeRange;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;

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

    public List<AgeRange> getAgeRanges() {
        return prismDAO.getDefinitions(AgeRange.class);
    }

    public List<Domicile> getDomiciles() {
        return prismDAO.getDefinitions(Domicile.class);
    }

}
