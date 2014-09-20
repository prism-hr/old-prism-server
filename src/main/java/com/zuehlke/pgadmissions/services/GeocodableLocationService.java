package com.zuehlke.pgadmissions.services;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.GeocodableLocation;
import com.zuehlke.pgadmissions.domain.GeographicLocation;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.exceptions.DeduplicationException;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse.Result.Geometry;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse.Result.Geometry.Location;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse.Result.Geometry.Viewport.Northeast;
import com.zuehlke.pgadmissions.google.jaxb.GeocodeResponse.Result.Geometry.Viewport.Southwest;
import com.zuehlke.pgadmissions.utils.ConversionUtils;

@Service
@Transactional
public class GeocodableLocationService {

    @Value("${integration.google.geocoding.api.uri}")
    private String googleGeocodeApiUri;

    @Value("${integration.google.geocoding.api.key}")
    private String googleGeocodeApiKey;

    @Value("${integration.google.geocoding.api.request.delay.ms}")
    private Integer googleGeocodeRequestDelayMs;

    @Autowired
    private EntityService entityService;

    public <T extends GeocodableLocation> T getById(Class<T> locationClass, Object id) {
        return (T) entityService.getById(locationClass, id);
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
    
    public synchronized <T extends GeocodableLocation> GeocodeResponse getLocation(T location, String address) throws InterruptedException, IOException,
            JAXBException {
        wait(googleGeocodeRequestDelayMs);
        String addressEncoded = URLEncoder.encode(address, "UTF-8");
        URL request = new DefaultResourceLoader().getResource(googleGeocodeApiUri + "xml?address=" + addressEncoded + "&key=" + googleGeocodeApiKey).getURL();
        JAXBContext jaxbContext = JAXBContext.newInstance(GeocodeResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (GeocodeResponse) JAXBIntrospector.getValue(unmarshaller.unmarshal(request));
    }

    @SuppressWarnings("unchecked")
    public <T extends GeocodableLocation> void setLocation(T location, GeocodeResponse response) {
        location = (T) getById(location.getClass(), location.getId());
        int precision = 14;

        Geometry geometry = response.getResult().getGeometry();
        Location gLocation = geometry.getLocation();
        Northeast gViewportNe = geometry.getViewport().getNortheast();
        Southwest gViewportSw = geometry.getViewport().getSouthwest();

        GeographicLocation geographicLocation = new GeographicLocation().withLocationX(ConversionUtils.floatToBigDecimal(gLocation.getLat(), precision))
                .withLocationY(ConversionUtils.floatToBigDecimal(gLocation.getLng(), precision))
                .withLocationViewNeX(ConversionUtils.floatToBigDecimal(gViewportNe.getLat(), precision))
                .withLocationViewNeY(ConversionUtils.floatToBigDecimal(gViewportNe.getLng(), precision))
                .withLocationViewSwX(ConversionUtils.floatToBigDecimal(gViewportSw.getLat(), precision))
                .withLocationViewSwY(ConversionUtils.floatToBigDecimal(gViewportSw.getLng(), precision));

        location.setLocation(geographicLocation);
    }
    
    public void setFallbackLocation(InstitutionDomicileRegion region) {
        region = getById(InstitutionDomicileRegion.class, region.getId());
        InstitutionDomicileRegion cursorRegion = null;
        
        for (int i = region.getNestedLevel(); i < 0; i--) {
            if (cursorRegion == null) {
                cursorRegion = region;
            } else {
                cursorRegion = cursorRegion.getParentRegion();
            }
            
            InstitutionDomicileRegion parentRegion = cursorRegion.getParentRegion();
            
            if (parentRegion != null) {
                GeographicLocation parentLocation = parentRegion.getLocation();
                if (parentLocation != null) {
                    region.setLocation(parentLocation);
                    return;
                }
            }
        }
        
        GeographicLocation countryLocation = region.getDomicile().getLocation();
        region.setLocation(countryLocation);
    }

}
