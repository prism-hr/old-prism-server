package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.institution.InstitutionAddress;
import com.zuehlke.pgadmissions.domain.institution.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.location.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO.Results;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO.Results.Geometry;
import com.zuehlke.pgadmissions.dto.json.LocationSearchResponseDTO.Results.Geometry.Location;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;

@Service
@Transactional
public class GeocodableLocationService {

    private static Logger logger = LoggerFactory.getLogger(GeocodableLocationService.class);

    @Value("${integration.google.api.key}")
    private String googleApiKey;

    @Value("${integration.google.geocoding.api.uri}")
    private String googleGeocodeApiUri;

    @Value("${integration.google.geocoding.api.request.delay.ms}")
    private Integer googleGeocodeRequestDelayMs;

    @Autowired
    private EntityService entityService;

    @Autowired
    private RestTemplate restTemplate;

    public <T extends GeocodableLocation> T getById(Class<T> locationClass, Object id) {
        return entityService.getById(locationClass, id);
    }

    public <T extends GeocodableLocation> T getOrCreate(T transientLocation) throws DeduplicationException {
        T persistentLocation = entityService.getDuplicateEntity(transientLocation);
        if (persistentLocation == null) {
            entityService.save(transientLocation);
            return transientLocation;
        }
        transientLocation.setLocation(persistentLocation.getLocation());
        return entityService.replace(persistentLocation, transientLocation);
    }

    public synchronized <T extends GeocodableLocation> LocationSearchResponseDTO getLocation(String address) throws InterruptedException, IOException {
        wait(googleGeocodeRequestDelayMs);
        String addressEncoded = URLEncoder.encode(address, "UTF-8");
        URI request = new DefaultResourceLoader().getResource(googleGeocodeApiUri + "json?address=" + addressEncoded + "&key=" + googleApiKey).getURI();
        return restTemplate.getForObject(request, LocationSearchResponseDTO.class);
    }

    public void setLocation(InstitutionAddress address) {
        try {
            List<String> addressTokens = Lists.reverse(address.getAddressTokens());

            for (int i = addressTokens.size(); i > 0; i--) {
                List<String> requestTokens = addressTokens.subList(0, i);

                LocationSearchResponseDTO response = getLocation(Joiner.on(", ").join(Lists.reverse(requestTokens)) + ", " + address.getDomicile().getName());
                if (response.getStatus().equals("OK")) {
                    setLocation(address, response);
                    return;
                }
            }

            InstitutionDomicile domicile = address.getDomicile();
            address.setLocation(domicile.getLocation());
        } catch (Exception e) {
            logger.error("Problem obtaining location for " + address.getLocationString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends GeocodableLocation> void setLocation(T transientLocation, LocationSearchResponseDTO response) {
        T geocodableLocation = (T) getById(transientLocation.getClass(), transientLocation.getId());

        Results result = response.getResults().get(0);
        Geometry geometry = result.getGeometry();
        Location gLocation = geometry.getLocation();
        Location gViewportNe = geometry.getViewPort().getNorthEast();
        Location gViewportSw = geometry.getViewPort().getSouthWest();

        GeographicLocation geographicLocation = new GeographicLocation().withGoogleId(result.getPlaceId()).withLocationX(gLocation.getLat())
                .withLocationY(gLocation.getLng()).withLocationViewNeX(gViewportNe.getLat()).withLocationViewNeY(gViewportNe.getLng())
                .withLocationViewSwX(gViewportSw.getLat()).withLocationViewSwY(gViewportSw.getLng());
        geocodableLocation.setLocation(geographicLocation);
    }

}
