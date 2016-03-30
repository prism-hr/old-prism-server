package uk.co.alumeni.prism.services;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newLinkedList;
import static uk.co.alumeni.prism.PrismConstants.OK;

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

import uk.co.alumeni.prism.dao.AddressDAO;
import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.address.AddressCoordinates;
import uk.co.alumeni.prism.domain.address.AddressLocation;
import uk.co.alumeni.prism.domain.address.AddressLocationPart;
import uk.co.alumeni.prism.domain.definitions.PrismDomicile;
import uk.co.alumeni.prism.dto.EntityLocationDTO;
import uk.co.alumeni.prism.dto.json.EstablishmentSearchResponseDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleAddressComponentDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleGeometryDTO;
import uk.co.alumeni.prism.dto.json.GoogleResultDTO.GoogleGeometryDTO.Location;
import uk.co.alumeni.prism.dto.json.LocationSearchResponseDTO;
import uk.co.alumeni.prism.rest.dto.AddressDTO;
import uk.co.alumeni.prism.services.helpers.PropertyLoader;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;

@Service
@Transactional
public class AddressService {

    private static Logger logger = LoggerFactory.getLogger(AddressService.class);

    private static final List<String> googleLocationTypes = Lists.newArrayList("country", "administrative_area_level_1", "administrative_area_level_2",
            "administrative_area_level_3", "administrative_area_level_4", "administrative_area_level_5", "political", "postal_town", "locality", "sublocality",
            "sublocality_level_1", "sublocality_level_2", "sublocality_level_3", "sublocality_level_4", "sublocality_level_5", "neighborhood", "premise",
            "subpremise", "airport");

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.places.api.uri}")
    private String googlePlacesApiUri;

    @Value("${integration.google.geocoding.api.uri}")
    private String googleGeocodeApiUri;

    @Value("${integration.google.geocoding.api.request.delay.ms}")
    private Integer googleGeocodeRequestDelayMs;

    @Inject
    private AddressDAO addressDAO;

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

    public Address getById(Integer id) {
        return entityService.getById(Address.class, id);
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
                newAddress.setAddressCoordinates(new AddressCoordinates().withLatitude(oldAddressCoordinates.getLatitude()).withLongitude(
                        oldAddressCoordinates.getLongitude()));
            }

            return newAddress;
        }
        return null;
    }

    public void updateAndGeocodeAddress(Address address, AddressDTO addressDTO) {
        updateAndGeocodeAddress(address, addressDTO, null);
    }

    public void updateAndGeocodeAddress(Address address, AddressDTO addressDTO, String establishmentName) {
        updateAddress(address, addressDTO);
        geocodeAddress(address, establishmentName);
    }

    public void updateGeocodeAndPersistAddress(Address address, AddressDTO addressDTO) {
        updateGeocodeAndPersistAddress(address, addressDTO, null);
    }

    public void updateGeocodeAndPersistAddress(Address address, AddressDTO addressDTO, String establishmentName) {
        updateAddress(address, addressDTO);
        persistAndGeocodeAddress(address, establishmentName);
    }

    public void persistAndGeocodeAddress(Address address, String establishmentName) {
        entityService.save(address);
        geocodeAddress(address, establishmentName);
    }

    public void updateAddress(Address address, AddressDTO addressDTO) {
        address.setAddressLine1(addressDTO.getAddressLine1());
        address.setAddressLine2(Strings.emptyToNull(addressDTO.getAddressLine2()));
        address.setAddressTown(addressDTO.getAddressTown());
        address.setAddressRegion(Strings.emptyToNull(addressDTO.getAddressRegion()));
        address.setAddressCode(Strings.emptyToNull(addressDTO.getAddressCode()));
        address.setDomicile(prismService.getDomicileById(addressDTO.getDomicile()));
        address.setGoogleId(addressDTO.getGoogleId());
    }

    public List<Integer> getAddressesWithNoLocationParts() {
        return addressDAO.getAddressesWithNoLocationParts();
    }

    public void geocodeAddressAsEstablishment(Integer addressId) throws Exception {
        geocodeAddressAsEstablishment(getById(addressId));
    }
    
    public LinkedHashMultimap<Integer, String> getAddressLocationIndex(List<EntityLocationDTO> entityLocations, int precision) {
        Integer entityId = null;
        int entityLocationCount = 0;
        
        LinkedHashMultimap<Integer, String> entityLocationIndex = LinkedHashMultimap.create();
        for (EntityLocationDTO entityLocation : entityLocations) {
            Integer thisEntityId = entityLocation.getId();
            entityLocationCount = equal(entityId, thisEntityId) ? (entityLocationCount + 1) : 0;
            entityId = thisEntityId;

            if (entityLocationCount < precision) {
                entityLocationIndex.put(entityId, entityLocation.getLocation());
            }
        }
        
        return entityLocationIndex;
    }

    private void geocodeAddress(Address address, String establishmentName) {
        try {
            if (!geocodeAddressAsEstablishment(address)) {
                geocodeAddressAsLocation(address, establishmentName);
            }
        } catch (Exception e) {
            logger.error("Problem obtaining location for " + address.toString(), e);
        }
    }

    private synchronized boolean geocodeAddressAsEstablishment(Address address) throws Exception {
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

    private void geocodeAddressAsLocation(Address address, String establishmentName) throws Exception {
        List<String> addressTokens = Lists.reverse(address.getAddressTokens());
        addressTokens.add(establishmentName);

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

        addressDAO.deleteAddressLocations(address);
        addressDAO.getOrphanAddressLocationParts().stream().forEach(lp -> entityService.delete(lp));

        List<GoogleAddressComponentDTO> componentData = addressData.getComponents();
        if (CollectionUtils.isNotEmpty(componentData)) {
            AddressLocationPart parent = null;
            List<String> partNames = newLinkedList();
            Set<AddressLocation> locations = address.getAddressLocations();
            for (GoogleAddressComponentDTO componentItem : Lists.reverse(componentData)) {
                if (CollectionUtils.containsAny(googleLocationTypes, componentItem.getTypes())) {
                    String name = componentItem.getName();
                    if (!partNames.contains(name)) {
                        partNames.add(name);
                        AddressLocationPart part = entityService
                                .getOrCreate(new AddressLocationPart().withParent(parent).withName(name).withNameIndex(Joiner.on("|").join(partNames)));
                        locations.add(entityService.getOrCreate(new AddressLocation().withAddress(address).withLocationPart(part)));
                        parent = part;
                    }
                }
            }
        }
    }

}
