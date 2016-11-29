package uk.co.alumeni.prism.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.alumeni.prism.dao.PrismDAO;
import uk.co.alumeni.prism.domain.AgeRange;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.domain.definitions.PrismOpportunityType;
import uk.co.alumeni.prism.domain.workflow.OpportunityType;

import javax.inject.Inject;
import java.util.List;

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

    public AgeRange getAgeRangeFromAge(Integer age) {
        return prismDAO.getAgeRangeFromAge(age);
    }

    public List<AgeRange> getAgeRanges() {
        return prismDAO.getDefinitions(AgeRange.class);
    }

    public List<Domicile> getDomiciles() {
        return prismDAO.getDefinitions(Domicile.class);
    }

}
