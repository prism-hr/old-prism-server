package com.zuehlke.pgadmissions.services.importers;

import java.net.Authenticator;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.dao.ImportedEntityDAO;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.exceptions.DataImportException;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryNameType;
import com.zuehlke.pgadmissions.iso.jaxb.CategoryType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryCodesType;
import com.zuehlke.pgadmissions.iso.jaxb.CountryType;
import com.zuehlke.pgadmissions.iso.jaxb.ShortNameType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionLocaleType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;
import com.zuehlke.pgadmissions.services.EntityService;

@Service
public class InstitutionDomicileImportService {

    private static final Logger log = LoggerFactory.getLogger(InstitutionDomicileImportService.class);

    @Value("${institutionDomicile.import.location}")
    private String importLocation;
    
    @Autowired
    private ImportedEntityDAO importedEntityDAO;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ApplicationContext applicationContext;

    public void importEntities() throws DataImportException {
        InstitutionDomicileImportService thisBean = applicationContext.getBean(InstitutionDomicileImportService.class);
        log.info("Starting the import from file: " + importLocation);

        try {
            List<CountryType> unmarshalled = thisBean.unmarshall(importLocation);

            thisBean.mergeDomiciles(unmarshalled);
        } catch (Exception e) {
            throw new DataImportException("Error during the import of file: " + importLocation, e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CountryType> unmarshall(final String fileLocation) throws Exception {
        try {
            URL fileUrl = new DefaultResourceLoader().getResource(fileLocation).getURL();
            JAXBContext jaxbContext = JAXBContext.newInstance(CountryCodesType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<CountryCodesType> unmarshaled = (JAXBElement<CountryCodesType>) unmarshaller.unmarshal(fileUrl);
            CountryCodesType countryCodes = (CountryCodesType) unmarshaled.getValue();
            return countryCodes.getCountry();
        } finally {
            Authenticator.setDefault(null);
        }
    }

    public void mergeDomiciles(List<CountryType> countries) throws DataImportException {
        InstitutionDomicileImportService thisBean = applicationContext.getBean(InstitutionDomicileImportService.class);

        thisBean.disableAllDomicilesAndRegions();

        for (CountryType country : countries) {
            String status = null;
            String countryName = null;
            String alpha2Code = null;
            List<SubdivisionType> subdivisions = Lists.newLinkedList();
            Map<Short, String> categories = Maps.newHashMap();
            for (JAXBElement<?> element : country.getAlpha2CodeOrAlpha3CodeOrNumericCode()) {
                String elementName = element.getName().getLocalPart();
                if (elementName.equals("status")) {
                    status = (String) element.getValue();
                } else if (elementName.equals("short-name")) {
                    ShortNameType shortName = (ShortNameType) element.getValue();
                    if (shortName.getLang3Code().equals("eng")) {
                        countryName = shortName.getValue();
                    }
                } else if (elementName.equals("alpha-2-code")) {
                    alpha2Code = (String) element.getValue();
                } else if (elementName.equals("subdivision")) {
                    subdivisions.add((SubdivisionType) element.getValue());
                } else if (elementName.equals("category")) {
                    CategoryType category = (CategoryType) element.getValue();
                    for (CategoryNameType categoryName : category.getCategoryName()) {
                        if (categoryName.getLang3Code().equals("eng")) {
                            categories.put(category.getId(), categoryName.getValue());
                        }
                    }
                }
            }

            if (status.equals("exceptionally-reserved") || status.equals("indeterminately-reserved")) {
                continue;
            }

            InstitutionDomicile institutionDomicile = new InstitutionDomicile().withId(alpha2Code).withName(countryName).withEnabled(true);
            entityService.merge(institutionDomicile);

            mergeRegions(institutionDomicile, subdivisions, categories);
        }
    }

    private void mergeRegions(InstitutionDomicile domicile, List<SubdivisionType> subdivisions, Map<Short, String> categories) {
        SubdivisionToRegionConverter converter = new SubdivisionToRegionConverter(domicile, null, categories);
        Iterable<InstitutionDomicileRegion> regions = Iterables.concat(Iterables.transform(subdivisions, converter));

        for (InstitutionDomicileRegion region : regions) {
            entityService.merge(region);
        }
    }


    private static class SubdivisionToRegionConverter implements Function<SubdivisionType, Iterable<InstitutionDomicileRegion>> {

        private InstitutionDomicile domicile;

        private InstitutionDomicileRegion parentRegion;

        private Map<Short, String> categories;

        private SubdivisionToRegionConverter(InstitutionDomicile domicile, InstitutionDomicileRegion parentRegion, Map<Short, String> categories) {
            this.domicile = domicile;
            this.parentRegion = parentRegion;
            this.categories = categories;
        }

        @Override
        public Iterable<InstitutionDomicileRegion> apply(SubdivisionType subdivision) {
            List<SubdivisionLocaleType> locales = subdivision.getSubdivisionLocale();
            String name = locales.get(0).getSubdivisionLocaleName();
            List<String> otherNames = Lists.newLinkedList();
            for (int i = 1; i < locales.size(); i++) {
                otherNames.add(locales.get(i).getSubdivisionLocaleName());
            }
            String otherName = null;
            if (!otherNames.isEmpty()) {
                otherName = Joiner.on(",").join(otherNames);
            }
            String regionType = categories.get(subdivision.getCategoryId());

            InstitutionDomicileRegion currentRegion = new InstitutionDomicileRegion().withId(subdivision.getSubdivisionCode().getValue()).withEnabled(true)
                    .withDomicile(domicile).withParentRegion(parentRegion).withName(name).withOtherName(otherName).withRegionType(regionType);

            if (subdivision.getSubdivision().isEmpty()) {
                return Lists.newArrayList(currentRegion);
            }
            SubdivisionToRegionConverter subConverter = new SubdivisionToRegionConverter(domicile, currentRegion, categories);
            Iterable<InstitutionDomicileRegion> subregions = Iterables.concat(Iterables.transform(subdivision.getSubdivision(), subConverter));
            return Iterables.concat(Collections.singleton(currentRegion), subregions);
        }
    }

    @Transactional
    public void disableAllDomicilesAndRegions() {
        importedEntityDAO.disableAllEntities(InstitutionDomicile.class);
        importedEntityDAO.disableAllEntities(InstitutionDomicileRegion.class);
    }

}
