package com.zuehlke.pgadmissions.services.helpers;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelper {
    
    @Autowired
    private AdvertService advertService;

    public void updateAdvertClosingDates() {
        LocalDate baseline = new LocalDate();
        List<Advert> adverts = advertService.getAdvertsWithElapsedClosingDates(baseline);

        for (Advert advert : adverts) {
            advertService.updateAdvertClosingDate(baseline, advert);
        }
    }
    
}
