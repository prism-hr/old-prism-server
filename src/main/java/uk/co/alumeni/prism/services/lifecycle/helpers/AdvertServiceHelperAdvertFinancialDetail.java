package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.AdvertService;

@Component
public class AdvertServiceHelperAdvertFinancialDetail extends PrismServiceHelperAbstract {

    @Inject
    private AdvertService advertService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<Integer> adverts = advertService.getAdvertsWithElapsedPayConversions(baseline);
        for (Integer advert : adverts) {
            updateFinancialDetailNormalization(advert);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void updateFinancialDetailNormalization(Integer advert) {
        if (!isShuttingDown()) {
            advertService.updateFinancialDetailNormalization(advert);
        }
    }

}
