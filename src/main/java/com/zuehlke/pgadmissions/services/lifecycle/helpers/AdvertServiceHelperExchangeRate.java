package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperExchangeRate extends PrismServiceHelperAbstract {

    @Inject
    private AdvertService advertService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<Integer> adverts = advertService.getAdvertsWithElapsedCurrencyConversions(baseline);
        for (Integer advert : adverts) {
            updateCurrencyConversion(advert);
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void updateCurrencyConversion(Integer advert) {
        if (!isShuttingDown()) {
            advertService.updateCurrencyConversion(advert);
        }
    }

}
