package com.zuehlke.pgadmissions.workflow.transition.creators;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.imported.ImportedInstitution;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Optional;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionCreator.class);

    @Inject
    private AdvertService advertService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private ImportedEntityService importedEntityService;

    @Inject
    private SystemService systemService;

    private BigDecimal minimumWage;

    @Override
    public Resource<?> create(User user, InstitutionDTO newResource) {
        System system = systemService.getSystem();

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(system, advertDTO, newResource.getName());

        ImportedInstitution importedInstitution = Optional.ofNullable(newResource.getImportedInstitutionId()).map(id -> importedEntityService.getById(ImportedInstitution.class, id)).orElse(null);

        Institution institution = new Institution().withUser(user).withParentResource(system).withAdvert(advert).withName(advert.getName())
                .withCurrency(newResource.getCurrency()).withBusinessYearStartMonth(newResource.getBusinessYearStartMonth())
                .withMinimumWage(minimumWage).withGoogleId(advert.getAddress().getGoogleId()).withUclInstitution(false)
                .withImportedInstitution(importedInstitution);

        resourceService.setResourceAttributes(institution, newResource);
        return institution;
    }

    @PostConstruct
    public void initializeMinimumWage() throws IOException {
        InputStream inputStream = new URL("http://ec.europa.eu/eurostat/SDMX/diss-web/rest/data/earn_mw_cur/S.NAC.UK").openStream();
        org.jsoup.nodes.Document document = Jsoup.parse(inputStream, "UTF8", "http://ec.europa.eu");
        Elements series = document.getElementsByTag("generic:Series");
        if (series.size() != 1) {
            minimumWage = new BigDecimal(1074.0);
            logger.error("Expected only one series, found: " + series.size() + ". Applying " + minimumWage + " (data as for 2015-B2)");
        }
        Element obs = series.first().getElementsByTag("generic:Obs").first();
        String minWage = obs.getElementsByTag("generic:ObsValue").attr("value");
        minimumWage = new BigDecimal(minWage);
    }

}
