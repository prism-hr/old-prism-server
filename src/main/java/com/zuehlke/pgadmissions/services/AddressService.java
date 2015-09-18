package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.PrismConstants.OK;

import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.address.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.imported.ImportedDomicile;
import com.zuehlke.pgadmissions.dto.json.EstablishmentSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO.GoogleGeometryDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO.GoogleGeometryDTO.Location;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressDTO;

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
    private ImportedEntityService importedEntityService;

    @Inject
    private RestTemplate restTemplate;

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
                setLocation(address, result.getGeometry());
                return true;
            }
        }

        return false;
    }

    public void geocodeAddressAsLocation(Address address, String establishment) throws Exception {
        List<String> addressTokens = Lists.reverse(address.getLocationTokens());
        addressTokens.add(establishment);
        for (int i = addressTokens.size(); i >= 0; i--) {
            List<String> requestTokens = addressTokens.subList(0, i);
            LocationSearchResponseDTO response = getGeocodeLocation(Joiner.on(", ").skipNulls().join(Lists.reverse(requestTokens)) + ", " + address.getDomicile().getName());
            if (response.getStatus().equals(OK)) {
                List<GoogleResultDTO> results = response.getResults();
                if (!results.isEmpty()) {
                    setLocation(address, results.get(0).getGeometry());
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
        address.setDomicile(importedEntityService.getById(ImportedDomicile.class, addressData.getDomicile().getId()));
        address.setGoogleId(addressData.getGoogleId());
        geocodeAddress(address, establishment);
    }

    private synchronized LocationSearchResponseDTO getGeocodeLocation(String address) throws Exception {
        wait(googleGeocodeRequestDelayMs);
        String addressEncoded = URLEncoder.encode(address, "UTF-8");
        URI request = new DefaultResourceLoader().getResource(googleGeocodeApiUri + "json?address=" + addressEncoded + "&key=" + googleApiKey).getURI();
        return restTemplate.getForObject(request, LocationSearchResponseDTO.class);
    }

    private void setLocation(Address address, GoogleGeometryDTO geometry) {
        Location googleLocation = geometry.getLocation();
        AddressCoordinates addressCoordinates = new AddressCoordinates().withLatitude(googleLocation.getLat()).withLongitude(googleLocation.getLng());
        address.setAddressCoordinates(addressCoordinates);
    }

}
