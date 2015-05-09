package com.zuehlke.pgadmissions.services;

import static com.zuehlke.pgadmissions.utils.PrismConstants.OK;

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
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.dto.json.EstablishmentSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO.GoogleGeometryDTO;
import com.zuehlke.pgadmissions.dto.json.GoogleResultDTO.GoogleGeometryDTO.Location;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO;

@Service
@Transactional
public class GeocodableLocationService {

    private static Logger logger = LoggerFactory.getLogger(GeocodableLocationService.class);

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.places.api.uri}")
    private String googlePlacesApiUri;

    @Value("${integration.google.geocoding.api.uri}")
    private String googleGeocodeApiUri;

    @Value("${integration.google.geocoding.api.request.delay.ms}")
    private Integer googleGeocodeRequestDelayMs;

    @Inject
    private RestTemplate restTemplate;

    public void setLocation(String googleIdentifier, String establishment, InstitutionAddress address) {
        try {
            if (googleIdentifier == null || !setEstablishmentLocation(googleIdentifier, address)) {
                setGeocodeLocation(googleIdentifier, establishment, address);
            }
        } catch (Exception e) {
            logger.error("Problem obtaining location for " + address.getLocationString(), e);
        }
    }

    public synchronized <T extends GeocodableLocation> EstablishmentSearchResponseDTO getEstablishmentLocation(String googleIdentifier) throws Exception {
        wait(googleGeocodeRequestDelayMs);
        URI request = new DefaultResourceLoader().getResource(googlePlacesApiUri + "json?placeid=" + googleIdentifier + "&key=" + googleApiKey).getURI();
        return restTemplate.getForObject(request, EstablishmentSearchResponseDTO.class);
    }

    private boolean setEstablishmentLocation(String googleIdentifier, InstitutionAddress address) throws Exception {
        EstablishmentSearchResponseDTO response = getEstablishmentLocation(googleIdentifier);
        if (response.getStatus().equals(OK)) {
            GoogleResultDTO result = response.getResult();
            if (result != null) {
                setLocation(googleIdentifier, address, result.getGeometry());
                return true;
            }
        }
        return false;
    }

    public synchronized <T extends GeocodableLocation> LocationSearchResponseDTO getGeocodeLocation(String address) throws Exception {
        wait(googleGeocodeRequestDelayMs);
        String addressEncoded = URLEncoder.encode(address, "UTF-8");
        URI request = new DefaultResourceLoader().getResource(googleGeocodeApiUri + "json?address=" + addressEncoded + "&key=" + googleApiKey).getURI();
        return restTemplate.getForObject(request, LocationSearchResponseDTO.class);
    }

    private void setGeocodeLocation(String googleIdentifier, String establishment, InstitutionAddress address) throws Exception {
        List<String> addressTokens = Lists.reverse(address.getAddressTokens());
        addressTokens.add(establishment);
        for (int i = addressTokens.size(); i >= 0; i--) {
            List<String> requestTokens = addressTokens.subList(0, i);
            LocationSearchResponseDTO response = getGeocodeLocation(Joiner.on(", ").join(Lists.reverse(requestTokens)) + ", " + address.getDomicile().getName());
            if (response.getStatus().equals(OK)) {
                List<GoogleResultDTO> results = response.getResults();
                if (!results.isEmpty()) {
                    setLocation(googleIdentifier, address, results.get(0).getGeometry());
                    return;
                }
            }
        }
    }

    private void setLocation(String googleIdentifier, GeocodableLocation location, GoogleGeometryDTO geometry) {
        Location googleLocation = geometry.getLocation();
        GeographicLocation geographicLocation = new GeographicLocation().withGoogleId(googleIdentifier).withLocationX(googleLocation.getLat())
                .withLocationY(googleLocation.getLng());
        location.setLocation(geographicLocation);
    }

}
