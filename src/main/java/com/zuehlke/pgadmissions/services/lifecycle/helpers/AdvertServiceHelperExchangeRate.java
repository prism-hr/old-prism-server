package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperExchangeRate extends AbstractServiceHelper {
    
    @Autowired
    private AdvertService advertService;
    
    @Override
    public void execute() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, JAXBException {
        LocalDate baseline = new LocalDate();
        List<Advert> adverts = advertService.getAdvertsWithElapsedCurrencyConversions(baseline);
        for (Advert advert : adverts) {
            advertService.updateCurrencyConversion(advert);
        }
    }

}
