package com.zuehlke.pgadmissions.workflow.transition.creators;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.resource.System;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.rest.dto.FileDTO;
import com.zuehlke.pgadmissions.rest.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.dto.advert.AdvertDTO;
import com.zuehlke.pgadmissions.services.AdvertService;
import com.zuehlke.pgadmissions.services.DocumentService;
import com.zuehlke.pgadmissions.services.ResourceService;
import com.zuehlke.pgadmissions.services.SystemService;

@Component
public class InstitutionCreator implements ResourceCreator<InstitutionDTO> {

    private static final Logger logger = LoggerFactory.getLogger(InstitutionCreator.class);

    @Inject
    private AdvertService advertService;

    @Inject
    private DocumentService documentService;

    @Inject
    private ResourceService resourceService;

    @Inject
    private SystemService systemService;

    private BigDecimal minimumWage;

    @Override
    public Resource create(User user, InstitutionDTO newResource) throws Exception {
        System system = systemService.getSystem();

        AdvertDTO advertDTO = newResource.getAdvert();
        Advert advert = advertService.createAdvert(system, advertDTO, newResource.getName());

        FileDTO logoImageDTO = newResource.getLogoImage();
        Document logoImage = logoImageDTO == null ? null : documentService.getById(logoImageDTO.getId());

        Institution institution = new Institution().withUser(user).withParentResource(system).withAdvert(advert).withName(advert.getName())
                .withCurrency(newResource.getCurrency()).withBusinessYearStartMonth(newResource.getBusinessYearStartMonth())
                .withMinimumWage(minimumWage).withGoogleId(advert.getAddress().getGoogleId()).withUclInstitution(false)
                .withLogoImage(logoImage);

        resourceService.setResourceAttributes(institution, newResource);
        return institution;
    }

    @PostConstruct
    public void initializeMinimumWage() throws IOException {
        InputStream inputStream = new URL("http://ec.europa.eu/eurostat/SDMX/diss-web/rest/data/earn_mw_cur/S.NAC.UK").openStream();
        org.jsoup.nodes.Document document = Jsoup.parse(inputStream, "UTF8", "http://ec.europa.eu");
        Elements series = document.getElementsByTag("generic:Series");
        if(series.size() != 1) {
            minimumWage = new BigDecimal(1074.0);
            logger.error("Expected only one series, found: " + series.size() + ". Applying " + minimumWage + " (data as for 2015-B2)");
        }
        Element obs = series.first().getElementsByTag("generic:Obs").first();
        String minWage = obs.getElementsByTag("generic:ObsValue").attr("value");
        minimumWage = new BigDecimal(minWage);
    }

}
