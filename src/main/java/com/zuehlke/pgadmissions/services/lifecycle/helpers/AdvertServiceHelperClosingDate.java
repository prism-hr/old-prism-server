package com.zuehlke.pgadmissions.services.lifecycle.helpers;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.services.AdvertService;

@Component
public class AdvertServiceHelperClosingDate implements PrismServiceHelper {

	@Inject
	private AdvertService advertService;

	@Override
	public void execute() {
		LocalDate baseline = new LocalDate();
		List<Integer> adverts = advertService.getAdvertsWithElapsedClosingDates(baseline);
		for (Integer advert : adverts) {
			advertService.refreshClosingDate(advert, baseline);
		}
	}

}
