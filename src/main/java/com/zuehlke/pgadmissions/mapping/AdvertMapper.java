package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;
import static com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType.valueOf;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAttribute;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.AddressCoordinates;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.domain.resource.ResourceOpportunity;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.domain.resource.department.Department;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.dto.imported.ImportedAdvertDomicileDTO;
import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressCoordinatesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCategoriesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertClosingDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertFinancialDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertFinancialDetailsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationExtended;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertRepresentationSimple;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertSubjectAreaRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.AdvertTargetsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.institution.ResourceRepresentationTarget;
import com.zuehlke.pgadmissions.services.AdvertService;

import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

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
            representation.setOpportunityType(valueOf(((ResourceOpportunity) resource).getOpportunityType().getName()));
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

        addressDTO.setDomicile(new ImportedAdvertDomicileDTO().withId(address.getDomicile().getId()));
        addressDTO.setGoogleId(address.getGoogleId());

        return addressDTO;
    }

    public List<ImportedAdvertDomicileResponse> getAdvertDomicileRepresentations() {
        List<ImportedAdvertDomicile> importedAdvertDomiciles = advertService.getAdvertDomiciles();
        return importedAdvertDomiciles.stream().map(this::getAdvertDomicileRepresentation).collect(Collectors.toList());
    }

    public AddressAdvertRepresentation getAdvertAddressRepresentation(Advert advert) {
        AddressAdvert address = advert.getAddress();
        if (address != null) {
            AddressAdvertRepresentation representation = addressMapper.transform(address, AddressAdvertRepresentation.class);

            representation.setDomicile(getAdvertDomicileRepresentation(address.getDomicile()));
            representation.setGoogleId(address.getGoogleId());

            AddressCoordinates addressCoordinates = address.getCoordinates();
            if (addressCoordinates != null) {
                representation.setCoordinates(new AddressCoordinatesRepresentation().withLatitude(addressCoordinates.getLatitude()).withLongitude(
                        addressCoordinates.getLongitude()));
            }

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
        if (targets != null) {
            return new AdvertTargetsRepresentation().withCompetences(getAdvertCompetenceRepresentations(targets.getCompetences()))
                    .withSubjectAreas(getAdvertSubjectAreaRepresentations(targets.getSubjectAreas()))
                    .withResources(getAdvertResourceRepresentations(advert, false)).withSelectedResources(getAdvertResourceRepresentations(advert, true));
        }
        return null;
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Collection<AdvertCompetence> competences) {
        return competences.stream().<AdvertCompetenceRepresentation> map(competence -> new AdvertCompetenceRepresentation().withName(competence.getName())
                .withDescription(competence.getValue().getDescription()).withImportance(competence.getImportance())).collect(Collectors.toList());
    }

    private List<AdvertSubjectAreaRepresentation> getAdvertSubjectAreaRepresentations(Collection<AdvertSubjectArea> subjectAreas) {
        return subjectAreas.stream().map(subjectArea -> new AdvertSubjectAreaRepresentation().withId(subjectArea.getValueId()).withName(subjectArea.getName()))
                .collect(Collectors.toList());
    }

    private List<ResourceRepresentationTarget> getAdvertResourceRepresentations(Advert advert, boolean selected) {
        return resourceMapper.getResourceTargetingRepresentations(advert, null, advertService.getAdvertTargetResources(advert, INSTITUTION, selected),
                advertService.getAdvertTargetResources(advert, DEPARTMENT, selected));
    }

    private ImportedAdvertDomicileResponse getAdvertDomicileRepresentation(ImportedAdvertDomicile domicile) {
        return new ImportedAdvertDomicileResponse().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

}
