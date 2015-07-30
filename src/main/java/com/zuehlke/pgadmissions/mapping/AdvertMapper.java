package com.zuehlke.pgadmissions.mapping;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.*;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.Coordinates;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.CoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.*;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;

@Service
@Transactional
public class AdvertMapper {

    @Inject
    private AdvertService advertService;

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    public AdvertRepresentationSimple getAdvertRepresentationSimple(Advert advert) {
        return getAdvertRepresentation(advert, AdvertRepresentationSimple.class);
    }

    public AdvertRepresentationExtended getAdvertRepresentationExtended(Advert advert) {
        AdvertRepresentationExtended representation = getAdvertRepresentation(advert, AdvertRepresentationExtended.class);

        ResourceParent resource = advert.getResource();
        representation.setUser(userMapper.getUserRepresentationSimple(resource.getUser()));
        representation.setResource(resourceMapper.getResourceRepresentationSimple(resource));

        Institution institution = resource.getInstitution();
        if (!institution.sameAs(resource)) {
            representation.setInstitution(resourceMapper.getResourceRepresentationSimple(institution));
        }

        Department department = resource.getDepartment();
        if (!(department == null || department.sameAs(resource))) {
            representation.setDepartment(resourceMapper.getResourceRepresentationSimple(resource));
        }

        if (ResourceOpportunity.class.isAssignableFrom(resource.getClass())) {
            representation.setOpportunityType(PrismOpportunityType.valueOf(((ResourceOpportunity) resource).getOpportunityType().getName()));
        }

        representation.setConditions(resourceMapper.getResourceConditionRepresentations(resource));

        representation.setName(advert.getName());
        return representation;
    }

    public <T extends AdvertRepresentationSimple> T getAdvertRepresentation(Advert advert, Class<T> returnType) {
        T representation = BeanUtils.instantiate(returnType);

        representation.setId(advert.getId());
        representation.setSummary(advert.getSummary());
        representation.setDescription(advert.getDescription());

        Integer backgroundImageId = advertService.getBackgroundImage(advert);
        representation.setBackgroundImage(backgroundImageId != null ? new DocumentRepresentation().withId(backgroundImageId) : null);
        representation.setHomepage(advert.getHomepage());
        representation.setApplyHomepage(advert.getApplyHomepage());

        representation.setTelephone(advert.getTelephone());
        representation.setAddress(getAdvertAddressRepresentation(advert));
        representation.setFinancialDetails(getAdvertFinancialDetailsRepresentation(advert));

        representation.setClosingDate(getAdvertClosingDateRepresentation(advert));
        representation.setClosingDates(getAdvertClosingDateRepresentations(advert));

        representation.setCategories(getAdvertCategoriesRepresentation(advert));
        representation.setTargets(getAdvertTargetsRepresentation(advert));

        representation.setSequenceIdentifier(advert.getSequenceIdentifier());
        return representation;
    }

    public AddressAdvertDTO getAddressDTO(AddressAdvert address) {
        AddressAdvertDTO addressDTO = addressMapper.transform(address, AddressAdvertDTO.class);

        addressDTO.setDomicile(address.getDomicile().getId());
        addressDTO.setGoogleId(address.getGoogleId());

        return addressDTO;
    }

    public List<ImportedAdvertDomicileResponse> getAdvertDomicileRepresentations() {
        List<ImportedAdvertDomicile> importedAdvertDomiciles = advertService.getAdvertDomiciles();
        return importedAdvertDomiciles.stream().map(this::getAdvertDomicileRepresentation).collect(Collectors.toList());
    }

    public List<AdvertRepresentationExtended> getRecommendedAdvertRepresentations(Application application) {
        List<AdvertRepresentationExtended> representations = Lists.newLinkedList();
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(application.getUser());
        for (AdvertRecommendationDTO advertRecommendation : advertRecommendations) {
            representations.add(getAdvertRepresentationExtended(advertRecommendation.getAdvert()));
        }
        return representations;
    }

    public AddressAdvertRepresentation getAdvertAddressRepresentation(Advert advert) {
        AddressAdvert address = advert.getAddress();
        if (address != null) {
            AddressAdvertRepresentation representation = addressMapper.transform(address, AddressAdvertRepresentation.class);

            representation.setDomicile(getAdvertDomicileRepresentation(address.getDomicile()));
            representation.setGoogleId(address.getGoogleId());

            Coordinates coordinates = address.getCoordinates();
            if (coordinates != null) {
                representation.setCoordinates(new CoordinatesRepresentation(coordinates.getLatitude(), coordinates.getLongitude()));
            }

            representation.setLocationString(address.getLocationString());
            return representation;
        }

        return null;
    }

    private AdvertFinancialDetailsRepresentation getAdvertFinancialDetailsRepresentation(Advert advert) {
        AdvertFinancialDetail fee = advert.getFee();
        AdvertFinancialDetail pay = advert.getPay();
        if (!(fee == null && pay == null)) {
            return new AdvertFinancialDetailsRepresentation().withFee(getAdvertFinancialDetailRepresentation(fee)).withPay(
                    getAdvertFinancialDetailRepresentation(pay));
        }
        return null;
    }

    private AdvertFinancialDetailRepresentation getAdvertFinancialDetailRepresentation(AdvertFinancialDetail detail) {
        if (detail != null) {
            PrismDurationUnit durationUnit = detail.getInterval();
            AdvertFinancialDetailRepresentation representation = new AdvertFinancialDetailRepresentation().withCurrency(detail.getCurrencySpecified())
                    .withInterval(detail.getInterval());
            if (durationUnit.equals(YEAR)) {
                representation.setMinimum(detail.getYearMinimumSpecified());
                representation.setMaximum(detail.getYearMaximumSpecified());
            } else {
                representation.setMinimum(detail.getMonthMinimumSpecified());
                representation.setMaximum(detail.getMonthMaximumSpecified());
            }
            return representation;
        }
        return null;
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(Advert advert) {
        AdvertClosingDate closingDate = advert.getClosingDate();
        return closingDate == null ? null : getAdvertClosingDateRepresentation(closingDate);
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(AdvertClosingDate closingDate) {
        return new AdvertClosingDateRepresentation().withId(closingDate.getId()).withClosingDate(closingDate.getValue());
    }

    private List<AdvertClosingDateRepresentation> getAdvertClosingDateRepresentations(Advert advert) {
        return advert.getClosingDates().stream().map(this::getAdvertClosingDateRepresentation).collect(Collectors.toList());
    }

    private AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(getAdvertCategoryRepresentations(categories.getIndustries()))
                .withFunctions(getAdvertCategoryRepresentations(categories.getFunctions()))
                .withThemes(getAdvertCategoryRepresentations(categories.getThemes()));
    }

    private <T extends AdvertAttribute<U>, U> List<U> getAdvertCategoryRepresentations(Set<T> categories) {
        return categories.stream().map(T::getValue).collect(Collectors.toList());
    }

    private AdvertTargetsRepresentation getAdvertTargetsRepresentation(Advert advert) {
        AdvertTargets targets = advertService.getAdvertTargets(advert);
        return targets == null ? null : new AdvertTargetsRepresentation().withCompetences(getAdvertCompetenceRepresentations(targets.getCompetences()))
                .withInstitutions(getAdvertTargetRepresentations(targets.getInstitutions()))
                .withDepartments(getAdvertTargetRepresentations(targets.getDepartments())).withPrograms(getAdvertTargetRepresentations(targets.getPrograms()))
                .withSubjectAreas(getAdvertTargetRepresentations(targets.getSubjectAreas()));
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Collection<AdvertCompetence> competences) {
        return competences
                .stream()
                .<AdvertCompetenceRepresentation> map(
                        competence -> new AdvertCompetenceRepresentation().withId(competence.getValueId()).withName(competence.getName())
                                .withDescription(competence.getValue().getDescription()))
                .collect(Collectors.toList());
    }

    private <T extends AdvertTarget<?>> List<AdvertTargetRepresentation> getAdvertTargetRepresentations(Set<T> targets) {
        return targets.stream()
                .<AdvertTargetRepresentation> map(target -> new AdvertTargetRepresentation().withId(target.getValueId()).withName(target.getName()))
                .collect(Collectors.toList());
    }

    private ImportedAdvertDomicileResponse getAdvertDomicileRepresentation(ImportedAdvertDomicile domicile) {
        return new ImportedAdvertDomicileResponse().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

}
