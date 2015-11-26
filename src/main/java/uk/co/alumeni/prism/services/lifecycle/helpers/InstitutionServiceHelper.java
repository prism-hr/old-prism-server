package uk.co.alumeni.prism.services.lifecycle.helpers;

import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.BooleanUtils.isTrue;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_HESA;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_UCAS;
import static uk.co.alumeni.prism.domain.definitions.PrismDisplayPropertyDefinition.SYSTEM_VALUE_NOT_SPECIFIED;
import static uk.co.alumeni.prism.domain.definitions.PrismOpportunityCategory.STUDY;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.SYSTEM;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Location;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Joiner;

import jersey.repackaged.com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.resource.System;
import uk.co.alumeni.prism.exceptions.WorkflowDuplicateResourceException;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.rest.dto.advert.AdvertDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionDTO;
import uk.co.alumeni.prism.rest.dto.resource.InstitutionImportDTO;
import uk.co.alumeni.prism.rest.dto.resource.ResourceDTO;
import uk.co.alumeni.prism.services.DocumentService;
import uk.co.alumeni.prism.services.InstitutionService;
import uk.co.alumeni.prism.services.PrismService;
import uk.co.alumeni.prism.services.SystemService;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Component
public class InstitutionServiceHelper extends PrismServiceHelperAbstract {

    private static Logger logger = LoggerFactory.getLogger(InstitutionServiceHelper.class);

    @Value("${import.on}")
    private Boolean importOn;

    @Value("${auth.facebook.clientId}")
    private String facebookClientId;

    @Value("${auth.facebook.appSecret}")
    private String facebookAppSecret;

    @Inject
    private DocumentService documentService;

    @Inject
    private InstitutionService institutionService;

    @Inject
    private PrismService prismService;

    @Inject
    private SystemService systemService;

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private ApplicationContext applicationContext;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() throws Exception {
        if (isTrue(importOn)) {
            System system = systemService.getSystem();

            FacebookServiceProvider facebookServiceProvider = new FacebookServiceProvider(facebookClientId, facebookAppSecret, null);
            AccessGrant accessGrant;
            try {
                accessGrant = facebookServiceProvider.getOAuthOperations().authenticateClient();
            } catch (ResourceAccessException e) {
                logger.error("Could not obtain Facebook token due to: " + e.getMessage());
                return;
            }
            Facebook facebookApi = facebookServiceProvider.getApi(accessGrant.getAccessToken());

            PropertyLoader loader = applicationContext.getBean(PropertyLoader.class).localizeLazy(system);
            getInstitutionRepresentations().forEach(ii -> {
                if (isNewInstitution(loader, ii.getUcasIds(), ii.getHesaId())) {
                    importInstitution(system, loader, ii, facebookApi);
                }
            });
        }
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void importInstitution(System system, PropertyLoader loader, InstitutionImportDTO institutionImport, Facebook facebookApi) {
        if (!isShuttingDown()) {
            logger.info("Importing institution: " + institutionImport.getName());

            String facebookId = institutionImport.getFacebookId();
            Page facebookPage = null;
            if (facebookId != null) {
                try {
                    facebookPage = facebookApi.pageOperations().getPage(facebookId);
                } catch (UncategorizedApiException e) {
                    logger.error("Could not read Facebook data for institution import ID: " + institutionImport.getName());
                }
            }
            facebookPage = ObjectUtils.firstNonNull(facebookPage, new Page());

            UcasInstitutionData ucasInstitutionData;
            if (institutionImport.getUcasIds() == null || institutionImport.getUcasIds().isEmpty()) {
                ucasInstitutionData = new UcasInstitutionData();
            } else {
                try {
                    ucasInstitutionData = getInstitutionData(institutionImport.getUcasIds().get(0));
                } catch (Exception e) {
                    logger.error("Could not read UCAS data for institution import ID: " + institutionImport.getName(), e);
                    return;
                }
            }

            AdvertDTO advertDTO = new AdvertDTO();
            InstitutionDTO institutionDTO = new InstitutionDTO();
            institutionDTO.setParentResource(new ResourceDTO().withId(system.getId()).withScope(SYSTEM));
            institutionDTO.setImportedCode(getImportedCode(loader, institutionImport.getUcasIds(), institutionImport.getHesaId()));
            institutionDTO.setCurrency("GBP");
            institutionDTO.setBusinessYearStartMonth(10);
            institutionDTO.setOpportunityCategories(Collections.singletonList(STUDY));
            institutionDTO.setName(institutionImport.getName());
            institutionDTO.setAdvert(advertDTO);
            AddressDTO address = new AddressDTO();
            advertDTO.setAddress(address);

            advertDTO.setSummary(createSummary(facebookPage, ucasInstitutionData.getSummary()));

            String placeholder = loader.loadLazy(SYSTEM_VALUE_NOT_SPECIFIED);
            advertDTO.setTelephone(firstNonNull(ucasInstitutionData.getTelephone(), facebookPage.getPhone(), placeholder));
            advertDTO.setHomepage(firstNonNull(ucasInstitutionData.getHomepage(), facebookPage.getWebsite(), placeholder));

            if (facebookPage.getLocation() != null) {
                Location location = facebookPage.getLocation();
                address.setDomicile(prismService.getDomicileByName(location.getCountry()));
                address.setAddressLine1(firstNonNull(location.getStreet(), placeholder));
                address.setAddressCode(firstNonNull(location.getZip(), placeholder));
                address.setAddressTown(firstNonNull(location.getCity(), placeholder));
            }

            AddressDTO ucasAddress = ucasInstitutionData.getAddress();
            if (ucasAddress != null) {
                address.setAddressLine1(firstNonNull(address.getAddressLine1(), ucasAddress.getAddressLine1(), placeholder));
                address.setAddressCode(firstNonNull(address.getAddressCode(), ucasAddress.getAddressCode(), placeholder));
                address.setAddressTown(firstNonNull(address.getAddressTown(), ucasAddress.getAddressTown(), placeholder));
                if (address.getDomicile() == null) {
                    address.setDomicile(ucasAddress.getDomicile());
                }
            }

            try {
                institutionService.createInstitution(system.getUser(), institutionDTO, facebookId, facebookPage);
            } catch (WorkflowDuplicateResourceException e) {
                logger.error("Could not import institution: " + institutionImport.getName(), e);
            }
        }
    }

    private boolean isNewInstitution(PropertyLoader loader, List<Integer> ucasIds, Integer hesaId) {
        String ucasPrefix = loader.loadLazy(SYSTEM_UCAS);
        if (isNotEmpty(ucasIds)) {
            for (Integer ucasId : ucasIds) {
                if (institutionService.getInstitutionByImportedCode(ucasPrefix + ucasId) != null) {
                    return false;
                }
            }
        }

        if (!(hesaId == null || institutionService.getInstitutionByImportedCode(loader.loadLazy(SYSTEM_HESA) + hesaId.toString()) == null)) {
            return false;
        }

        return true;
    }

    private String getImportedCode(PropertyLoader loader, List<Integer> ucasIds, Integer hesaId) {
        List<String> parts = newArrayList();
        String ucasPrefix = loader.loadLazy(SYSTEM_UCAS);
        if (ucasIds != null) {
            ucasIds.forEach(ucasId -> parts.add(ucasPrefix + ucasId.toString()));
        }

        if (hesaId != null) {
            parts.add(loader.loadLazy(SYSTEM_HESA) + hesaId.toString());
        }

        return Joiner.on("|").join(parts);
    }

    private String createSummary(Page facebookPage, String ucasSummary) {
        if (ucasSummary != null && ucasSummary.length() > 10) {
            return ucasSummary;
        }
        String about = facebookPage.getAbout();
        String description = facebookPage.getDescription();
        if (description == null) {
            return about;
        }
        if (about != null && about.length() > 100 && !about.toLowerCase().contains("facebook")) {
            return about;
        }

        List<String> paragraphs = Stream.of(description.split("\n\n")).filter(p -> !p.toLowerCase().contains("facebook")).collect(Collectors.toList());
        if (paragraphs.isEmpty()) {
            return Objects.firstNonNull(about, ucasSummary);
        }
        String summaryBuilder = paragraphs.get(0);
        for (String paragraph : paragraphs.subList(1, paragraphs.size())) {
            if ((summaryBuilder + "\n\n" + paragraph).length() > 1000) {
                return summaryBuilder;
            }
            summaryBuilder += "\n\n" + paragraph;
        }
        return summaryBuilder;
    }

    private UcasInstitutionData getInstitutionData(Integer ucasId) throws IOException {
        Document document = Jsoup.connect("http://search.ucas.com/provider/" + ucasId).get();

        AddressDTO addressDTO = null;
        String telephone = null;
        String homepage = null;

        Element addressElement = document.getElementsByClass("provcontactaddress").first();
        if (addressElement != null) {
            addressDTO = new AddressDTO();
            String countryString = Optional.ofNullable(addressElement.getElementById("country")).map(e -> e.text()).orElse(null);
            addressDTO.setAddressLine1(Optional.ofNullable(addressElement.getElementById("street")).map(e -> e.text()).orElse(null));
            Element townElement = ObjectUtils.firstNonNull(addressElement.getElementById("town"), addressElement.getElementById("locality"),
                    addressElement.getElementById("county"));
            addressDTO.setAddressTown(Optional.ofNullable(townElement).map(e -> e.text()).orElse(null));
            addressDTO.setAddressCode(Optional.ofNullable(addressElement.getElementById("postCode")).map(e -> e.text()).orElse(null));
            addressDTO.setAddressRegion(Optional.ofNullable(addressElement.getElementById("county")).map(e -> e.text()).orElse(null));
            addressDTO.setDomicile(prismService.getDomicileByName(countryString));
        }

        Element contactElement = document.getElementsByClass("provider_details_contact").first();
        if (contactElement != null) {
            telephone = Optional.ofNullable(contactElement.select("li.provider_contact_tel").first()).map(e -> e.getElementsByTag("span").last().text()).orElse(null);
            homepage = Optional.ofNullable(contactElement.select("li.provider_contact_web").first()).map(e -> e.getElementsByTag("a").first().text()).orElse(null);
        }

        String summary = Optional.ofNullable(document.getElementById("marketing")).map(e -> e.text()).orElse(null);

        return new UcasInstitutionData(addressDTO, telephone, homepage, summary);
    }

    private List<InstitutionImportDTO> getInstitutionRepresentations() throws Exception {
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, InstitutionImportDTO.class);
        return objectMapper.readValue(documentService.getAmazonClient().getObject("prism-import-data", "institution.json").getObjectContent(), collectionType);
    }

    private static class UcasInstitutionData {

        private AddressDTO address;

        private String telephone;

        private String homepage;

        private String summary;

        public UcasInstitutionData() {
        }

        public UcasInstitutionData(AddressDTO address, String telephone, String homepage, String summary) {
            this.address = address;
            this.telephone = telephone;
            this.homepage = homepage;
            this.summary = summary;
        }

        public AddressDTO getAddress() {
            return address;
        }

        public String getTelephone() {
            return telephone;
        }

        public String getHomepage() {
            return homepage;
        }

        public String getSummary() {
            return summary;
        }
    }

}
