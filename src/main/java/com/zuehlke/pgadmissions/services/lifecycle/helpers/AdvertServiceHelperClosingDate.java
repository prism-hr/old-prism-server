package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdvertServiceHelperClosingDate implements AbstractServiceHelper {

    @Autowired
    private AdvertService advertService;

    @Override
    public void execute() {
        LocalDate baseline = new LocalDate();
        List<Advert> adverts = advertService.getAdvertsWithElapsedClosingDates(baseline);
        for (Advert advert : adverts) {
            advertService.updateClosingDate(advert, baseline);
        }
    }

}

