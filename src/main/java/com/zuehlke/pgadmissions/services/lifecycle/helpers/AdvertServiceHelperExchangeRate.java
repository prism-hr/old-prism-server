package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.services.AdvertService;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class AdvertServiceHelperExchangeRate implements AbstractServiceHelper {

    @Inject
    private AdvertService advertService;

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<Integer> adverts = advertService.getAdvertsWithElapsedCurrencyConversions(baseline);
        for (Integer advert : adverts) {
            advertService.updateCurrencyConversion(advert);
        }
    }

}
