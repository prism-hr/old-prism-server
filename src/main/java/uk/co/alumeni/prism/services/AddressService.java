package uk.co.alumeni.prism.services;

import static uk.co.alumeni.prism.PrismConstants.MAX_INDEXABLE_COLUMN_LENGTH;
import static uk.co.alumeni.prism.PrismConstants.OK;
import static uk.co.alumeni.prism.domain.definitions.PrismAddressLocationPartType.getAddressLocationPartType;

import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.address.AddressCoordinates;
import uk.co.alumeni.prism.domain.address.AddressLocationPart;
import uk.co.alumeni.prism.domain.definitions.PrismAddressLocationPartType;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.dto.json.EstablishmentSearchResponseDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleAddressComponentDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleGeometryDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleGeometryDTO.Location;
import uk.co.alumeni.prism.dto.json.LocationSearchResponseDTO;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

@Service
@Transactional
public class AddressService {

    private static Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.places.api.uri}")
    private String googlePlacesApiUri;

    @Value("${integration.google.geocoding.api.uri}")
    private String googleGeocodeApiUri;

    @Value("${integration.google.geocoding.api.request.delay.ms}")
    private Integer googleGeocodeRequestDelayMs;

    @Inject
    private EntityService entityService;

    @Inject
    private PrismService prismService;

    @Inject
    private SystemService systemService;

    @Inject
    private RestTemplate restTemplate;

    @Inject
    private ApplicationContext applicationContext;

    public void geocodeAddress(Address address, String establishment) {
        try {
            if (!geocodeAddressAsEstablishment(address)) {
                geocodeAddressAsLocation(address, establishment);
            }
        } catch (Exception e) {
            logger.error("Problem obtaining location for " + address.getLocationString(), e);
        }
    }

    public synchronized boolean geocodeAddressAsEstablishment(Address address) throws Exception {
        wait(googleGeocodeRequestDelayMs);
        URI request = new DefaultResourceLoader().getResource(googlePlacesApiUri + "json?placeid=" + address.getGoogleId() + "&key=" + googleApiKey).getURI();
        EstablishmentSearchResponseDTO response = restTemplate.getForObject(request, EstablishmentSearchResponseDTO.class);

        if (response.getStatus().equals(OK)) {
            GoogleResultDTO result = response.getResult();
            if (result != null) {
                setLocation(address, result);
                return true;
            }
        }

        return false;
    }

    public void geocodeAddressAsLocation(Address address, String establishment) throws Exception {
        List<String> addressTokens = Lists.reverse(address.getLocationTokens());
        addressTokens.add(establishment);

        Domicile domicile = address.getDomicile();
        PrismDomicile prismDomicile = domicile == null ? null : domicile.getId();
        String domicileName = prismDomicile == null ? null
                : applicationContext.getBean(PropertyLoader.class).localizeLazy(systemService.getSystem()).loadLazy(prismDomicile.getDisplayProperty());

        for (int i = addressTokens.size(); i >= 0; i--) {
            List<String> requestTokens = addressTokens.subList(0, i);
            requestTokens.add(domicileName);

            LocationSearchResponseDTO response = getGeocodeLocation(Joiner.on(", ").skipNulls().join(Lists.reverse(requestTokens)));
            if (response.getStatus().equals(OK)) {
                List<GoogleResultDTO> results = response.getResults();
                if (!results.isEmpty()) {
                    setLocation(address, results.get(0));
                    return;
                }
            }
        }
    }

    public Address cloneAddress(Address oldAddress) {
        if (oldAddress != null) {
            Address newAddress = new Address();
            newAddress.setAddressLine1(oldAddress.getAddressLine1());
            newAddress.setAddressLine2(oldAddress.getAddressLine2());
            newAddress.setAddressTown(oldAddress.getAddressTown());
            newAddress.setAddressRegion(oldAddress.getAddressRegion());
            newAddress.setAddressCode(oldAddress.getAddressCode());
            newAddress.setDomicile(oldAddress.getDomicile());
            newAddress.setGoogleId(oldAddress.getGoogleId());

            AddressCoordinates oldAddressCoordinates = oldAddress.getAddressCoordinates();
            if (oldAddressCoordinates != null) {
                newAddress.setAddressCoordinates(new AddressCoordinates().withLatitude(oldAddressCoordinates.getLatitude()).withLongitude(oldAddressCoordinates.getLongitude()));
            }

            return newAddress;
        }
        return null;
    }

    public void copyAddress(Address address, AddressDTO addressData) {
        copyAddress(address, addressData, null);
    }

    public void copyAddress(Address address, AddressDTO addressData, String establishment) {
        address.setAddressLine1(addressData.getAddressLine1());
        address.setAddressLine2(Strings.emptyToNull(addressData.getAddressLine2()));
        address.setAddressTown(addressData.getAddressTown());
        address.setAddressRegion(Strings.emptyToNull(addressData.getAddressRegion()));
        address.setAddressCode(Strings.emptyToNull(addressData.getAddressCode()));
        address.setDomicile(prismService.getDomicileById(addressData.getDomicile()));
        address.setGoogleId(addressData.getGoogleId());
        geocodeAddress(address, establishment);
    }

    private synchronized LocationSearchResponseDTO getGeocodeLocation(String address) throws Exception {
        wait(googleGeocodeRequestDelayMs);
        String addressEncoded = URLEncoder.encode(address, "UTF-8");
        URI request = new DefaultResourceLoader().getResource(googleGeocodeApiUri + "json?address=" + addressEncoded + "&key=" + googleApiKey).getURI();
        return restTemplate.getForObject(request, LocationSearchResponseDTO.class);
    }

    private void setLocation(Address address, GoogleResultDTO addressData) {
        GoogleGeometryDTO geometryData = addressData.getGeometry();
        if (geometryData != null) {
            Location googleLocation = geometryData.getLocation();
            AddressCoordinates addressCoordinates = new AddressCoordinates().withLatitude(googleLocation.getLat()).withLongitude(googleLocation.getLng());
            address.setAddressCoordinates(addressCoordinates);
        }

        List<GoogleAddressComponentDTO> componentData = addressData.getComponents();
        if (CollectionUtils.isNotEmpty(componentData)) {
            AddressLocationPart parent = null;
            Set<AddressLocationPart> parts = address.getAddressLocationParts();
            for (GoogleAddressComponentDTO componentItem : Lists.reverse(componentData)) {
                PrismAddressLocationPartType partType = getAddressLocationPartType(componentItem.getTypes());
                if (partType != null) {
                    String name = componentItem.getName();
                    AddressLocationPart part = entityService.getOrCreate(
                            new AddressLocationPart().withParent(parent).withType(partType).withName(name).withNameIndex(name.substring(0, MAX_INDEXABLE_COLUMN_LENGTH)));
                    parts.add(part);
                    parent = part;
                }
            }
        }
    }

}
