package com.zuehlke.pgadmissions.mapping;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.address.AddressAdvert;
import com.zuehlke.pgadmissions.domain.advert.*;
import com.zuehlke.pgadmissions.domain.application.Application;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.imported.ImportedAdvertDomicile;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.dto.AdvertRecommendationDTO;
import com.zuehlke.pgadmissions.rest.dto.AddressAdvertDTO;
import com.zuehlke.pgadmissions.rest.representation.address.AddressAdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.advert.*;
import com.zuehlke.pgadmissions.services.AdvertService;
import org.springframework.stereotype.Service;
import uk.co.alumeni.prism.api.model.imported.response.ImportedAdvertDomicileResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
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

    public AdvertRepresentation getAdvertRepresentation(Advert advert) {
        ResourceParent resource = advert.getResource();
        Department department = resource.getDepartment();

        return new AdvertRepresentation().withId(advert.getId()).withUser(userMapper.getUserRepresentationSimple(resource.getUser()))
                .withResource(resourceMapper.getResourceRepresentationSimple(resource))
                .withInstitution(resourceMapper.getResourceRepresentationSimple(resource.getInstitution()))
                .withDepartment(department == null ? null : resourceMapper.getResourceRepresentationSimple(department))
                .withOpportunityType(advert.getOpportunityType()).withTitle(advert.getTitle()).withSummary(advert.getSummary())
                .withDescription(advert.getDescription()).withHomepage(advert.getHomepage()).withApplyHomepage(advert.getApplyHomepage())
                .withTelephone(advert.getTelephone()).withAddress(getAdvertAddressRepresentation(advert))
                .withFinancialDetails(getAdvertFinancialDetailsRepresentation(advert)).withClosingDate(getAdvertClosingDateReprentation(advert))
                .withClosingDates(getAdvertClosingDateReprentations(advert)).withCategories(getAdvertCategoriesRepresentation(advert))
                .withTargets(getAdvertTargetsRepresentation(advert)).withSequenceIdentifier(advert.getSequenceIdentifier());
    }

    public AddressAdvertDTO getAddressDTO(AddressAdvert address) {
        AddressAdvertDTO addressDTO = addressMapper.transform(address, AddressAdvertDTO.class);

        addressDTO.setDomicile(address.getDomicile().getId());
        addressDTO.setGoogleId(address.getGoogleId());

        return addressDTO;
    }

    public List<ImportedAdvertDomicileResponse> getAdvertDomicileRepresentations() {
        List<ImportedAdvertDomicile> importedAdvertDomiciles = advertService.getAdvertDomiciles();
        List<ImportedAdvertDomicileResponse> representations = Lists.newLinkedList();
        for (ImportedAdvertDomicile importedAdvertDomicile : importedAdvertDomiciles) {
            representations.add(getAdvertDomicileRepresentation(importedAdvertDomicile));
        }
        return representations;
    }

    public List<AdvertRepresentation> getRecommendedAdvertRepresentations(Application application) {
        List<AdvertRecommendationDTO> advertRecommendations = advertService.getRecommendedAdverts(application.getUser());
        return advertRecommendations.stream()
                .map(advertRecommendation -> getAdvertRepresentation(advertRecommendation.getAdvert()))
                .collect(Collectors.toList());
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

    private AdvertClosingDateRepresentation getAdvertClosingDateReprentation(Advert advert) {
        AdvertClosingDate closingDate = advert.getClosingDate();
        return closingDate == null ? null : getAdvertClosingDateRepresentation(closingDate);
    }

    private AdvertClosingDateRepresentation getAdvertClosingDateRepresentation(AdvertClosingDate closingDate) {
        return new AdvertClosingDateRepresentation().withId(closingDate.getId()).withClosingDate(closingDate.getClosingDate());
    }

    private List<AdvertClosingDateRepresentation> getAdvertClosingDateReprentations(Advert advert) {
        List<AdvertClosingDateRepresentation> representations = Lists.newLinkedList();
        for (AdvertClosingDate closingDate : advert.getClosingDates()) {
            representations.add(getAdvertClosingDateRepresentation(closingDate));
        }
        return representations;
    }

    private AdvertCategoriesRepresentation getAdvertCategoriesRepresentation(Advert advert) {
        AdvertCategories categories = advertService.getAdvertCategories(advert);
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(getAdvertCategoryRepresentations(categories.getIndustries()))
                .withFunctions(getAdvertCategoryRepresentations(categories.getFunctions()))
                .withThemes(getAdvertCategoryRepresentations(categories.getThemes()));
    }

    private <T extends AdvertAttribute<U>, U> List<U> getAdvertCategoryRepresentations(Set<T> categories) {
        List<U> representations = Lists.newLinkedList();
        for (T category : categories) {
            representations.add(category.getValue());
        }
        return representations;
    }

    private AdvertTargetsRepresentation getAdvertTargetsRepresentation(Advert advert) {
        AdvertTargets targets = advertService.getAdvertTargets(advert);
        return targets == null ? null : new AdvertTargetsRepresentation().withCompetences(getAdvertCompetenceRepresentations(targets.getCompetences()))
                .withInstitutions(getAdvertTargetRepresentations(targets.getInstitutions()))
                .withDepartments(getAdvertTargetRepresentations(targets.getDepartments())).withPrograms(getAdvertTargetRepresentations(targets.getPrograms()))
                .withSubjectAreas(getAdvertTargetRepresentations(targets.getSubjectAreas()));
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetenceRepresentations(Set<AdvertCompetence> competences) {
        List<AdvertCompetenceRepresentation> representations = Lists.newLinkedList();
        for (AdvertCompetence competence : competences) {
            representations.add(new AdvertCompetenceRepresentation().withId(competence.getId()).withTitle(competence.getTitle())
                    .withDescription(competence.getValue().getDescription()).withImportance(competence.getImportance()));
        }
        return representations;
    }

    private <T extends AdvertTarget<?>> List<AdvertTargetRepresentation> getAdvertTargetRepresentations(Set<T> targets) {
        List<AdvertTargetRepresentation> representations = Lists.newLinkedList();
        for (AdvertTarget<?> target : targets) {
            representations.add(new AdvertTargetRepresentation().withId(target.getId()).withTitle(target.getTitle()).withImportance(target.getImportance()));
        }
        return representations;
    }

    private AddressAdvertRepresentation getAdvertAddressRepresentation(Advert advert) {
        AddressAdvert address = advert.getAddress();
        if (address != null) {
            AddressAdvertRepresentation representation = addressMapper.transform(address, AddressAdvertRepresentation.class);

            representation.setDomicile(getAdvertDomicileRepresentation(address.getDomicile()));
            representation.setGoogleId(address.getGoogleId());

            GeographicLocation location = address.getLocation();
            if (location != null) {
                representation.setLocationX(location.getLocationX());
                representation.setLocationY(location.getLocationY());
            }

            representation.setLocationString(address.getLocationString());
            return representation;
        }

        return null;
    }

    private ImportedAdvertDomicileResponse getAdvertDomicileRepresentation(ImportedAdvertDomicile domicile) {
        return new ImportedAdvertDomicileResponse().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

}
