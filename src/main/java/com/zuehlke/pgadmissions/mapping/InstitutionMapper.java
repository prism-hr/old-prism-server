package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.utils.PrismConstants.GEOCODING_PRECISION;
import static com.zuehlke.pgadmissions.utils.PrismConstants.TARGETING_PRECISION;
import static com.zuehlke.pgadmissions.utils.PrismConversionUtils.decimalObjectToBigDecimal;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.dto.InstitutionDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationClient;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationLocation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.InstitutionRepresentationTargeting;

@Service
@Transactional
public class InstitutionMapper {

    @Value("${system.id}")
    private Integer systemId;

    @Inject
    private AdvertMapper advertMapper;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    public InstitutionRepresentation getInstitutionRepresentation(Institution institution) {
        return getInstitutionRepresentation(institution, InstitutionRepresentation.class);
    }

    public InstitutionRepresentationLocation getInstitutionRepresentationLocation(Institution institution) {
        return getInstitutionRepresentationLocation(institution, InstitutionRepresentationLocation.class);
    }

    public InstitutionRepresentationLocation getInstitutionRepresentationLocation(InstitutionDTO<?> institution) {
        return getInstitutionRepresentationLocation(institution, InstitutionRepresentationLocation.class);
    }

    public InstitutionRepresentationTargeting getInstitutionRepresentationTargeting(InstitutionDTO<?> institution) {
        InstitutionRepresentationTargeting representation = getInstitutionRepresentationLocation(institution, InstitutionRepresentationTargeting.class);
        representation.setRelevance(decimalObjectToBigDecimal(institution.getTargetingRelevance(), TARGETING_PRECISION));
        representation.setDistance(decimalObjectToBigDecimal(institution.getTargetingDistance(), TARGETING_PRECISION));
        return representation;
    }

    public InstitutionRepresentationClient getInstitutionRepresentationClient(Institution institution) {
        InstitutionRepresentationClient representation = getInstitutionRepresentation(institution, InstitutionRepresentationClient.class);
        resourceMapper.appendResourceSummaryRepresentation(institution, representation);
        return representation;
    }

    private <T extends InstitutionRepresentationLocation> T getInstitutionRepresentationLocation(Institution institution, Class<T> returnType) {
        T representation = resourceMapper.getResourceRepresentationSimple(institution, returnType);
        representation.setAddress(advertMapper.getAdvertAddressRepresentation(institution.getAdvert()));
        return representation;
    }

    private <T extends InstitutionRepresentationLocation> T getInstitutionRepresentationLocation(InstitutionDTO<?> institution, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(institution.getId());
        representation.setName(institution.getName());
        representation.setLogoImage(new DocumentRepresentation().withId(institution.getLogoImageId()));

        representation.setAddress(new AddressAdvertRepresentation().withDomicile(new ImportedAdvertDomicileResponse()
                .withName(institution.getAddressDomicileName())).withAddressLine1(institution.getAddressLine1())
                .withAddressLine2(institution.getAddressLine2()).withAddressTown(institution.getAddressTown())
                .withAddressRegion(institution.getAddressRegion()).withAddressCode(institution.getAddressCode())
                .withGoogleId(institution.getAddressGoogleId()).withCoordinates(new AddressCoordinatesRepresentation()
                        .withLatitude(decimalObjectToBigDecimal(institution.getAddressCoordinateLatitude(), GEOCODING_PRECISION))
                        .withLongitude(decimalObjectToBigDecimal(institution.getAddressCoordinateLongitude(), GEOCODING_PRECISION))));

        return representation;
    }

    private <T extends InstitutionRepresentation> T getInstitutionRepresentation(Institution institution, Class<T> returnType) {
        T representation = resourceMapper.getResourceParentRepresentation(institution, returnType);
        representation.setCurrency(institution.getCurrency());
        representation.setBusinessYearStartMonth(institution.getBusinessYearStartMonth());
        return representation;
    }

}
