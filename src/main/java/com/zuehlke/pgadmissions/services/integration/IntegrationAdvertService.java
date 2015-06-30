package com.zuehlke.pgadmissions.services.integration;

import static com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit.YEAR;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.advert.Advert;
import com.zuehlke.pgadmissions.domain.advert.AdvertAddress;
import com.zuehlke.pgadmissions.domain.advert.AdvertCategories;
import com.zuehlke.pgadmissions.domain.advert.AdvertClosingDate;
import com.zuehlke.pgadmissions.domain.advert.AdvertCompetence;
import com.zuehlke.pgadmissions.domain.advert.AdvertDepartment;
import com.zuehlke.pgadmissions.domain.advert.AdvertDomicile;
import com.zuehlke.pgadmissions.domain.advert.AdvertFinancialDetail;
import com.zuehlke.pgadmissions.domain.advert.AdvertInstitution;
import com.zuehlke.pgadmissions.domain.advert.AdvertProgram;
import com.zuehlke.pgadmissions.domain.advert.AdvertSubjectArea;
import com.zuehlke.pgadmissions.domain.advert.AdvertTarget;
import com.zuehlke.pgadmissions.domain.advert.AdvertTargets;
import com.zuehlke.pgadmissions.domain.definitions.PrismDurationUnit;
import com.zuehlke.pgadmissions.domain.location.GeographicLocation;
import com.zuehlke.pgadmissions.domain.resource.Department;
import com.zuehlke.pgadmissions.domain.resource.ResourceParent;
import com.zuehlke.pgadmissions.rest.dto.AdvertAddressDTO;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertCategoriesRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertClosingDateRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertCompetenceRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertDomicileRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertFinancialDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertFinancialDetailsRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertTargetRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.advert.AdvertTargetsRepresentation;
import com.zuehlke.pgadmissions.services.AdvertService;

@Service
@Transactional
public class IntegrationAdvertService {

    @Inject
    private AdvertService advertService;

    @Inject
    private IntegrationResourceService integrationResourceService;

    @Inject
    private IntegrationUserService integrationUserService;

    public AdvertRepresentation getAdvertRepresentation(Advert advert) {
        ResourceParent resource = advert.getResource();
        Department department = resource.getDepartment();

        return new AdvertRepresentation().withId(advert.getId()).withUser(integrationUserService.getUserRepresentationSimple(resource.getUser()))
                .withResource(integrationResourceService.getResourceRepresentationSimple(resource))
                .withInstitution(integrationResourceService.getResourceRepresentationSimple(resource.getInstitution()))
                .withDepartment(department == null ? null : integrationResourceService.getResourceRepresentationSimple(department))
                .withOpportunityType(advert.getOpportunityType()).withTitle(advert.getTitle()).withSummary(advert.getSummary())
                .withDescription(advert.getDescription()).withHomepage(advert.getHomepage()).withApplyHomepage(advert.getApplyHomepage())
                .withTelephone(advert.getTelephone()).withAddress(getAdvertAddressRepresentation(advert))
                .withFinancialDetails(getAdvertFinancialDetailsRepresentation(advert)).withClosingDate(getAdvertClosingDateReprentation(advert))
                .withClosingDates(getAdvertClosingDateReprentations(advert)).withCategories(getAdvertCategoriesRepresentation(advert))
                .withTargets(getAdvertTargetsRepresentation(advert)).withSequenceIdentifier(advert.getSequenceIdentifier());
    }

    public AdvertAddressDTO getAddressDTO(AdvertAddress address) {
        return new AdvertAddressDTO().withDomicile(address.getDomicile().getId()).withAddressLine1(address.getAddressLine1())
                .withAddressLine2(address.getAddressLine2()).withAddressTown(address.getAddressTown()).withAddressRegion(address.getAddressRegion())
                .withAddressCode(address.getAddressCode()).withGoogleId(address.getGoogleId());
    }
    
    public List<AdvertDomicileRepresentation> getAdvertDomicileRepresentations() {
        List<AdvertDomicile> advertDomiciles = advertService.getAdvertDomiciles();
        List<AdvertDomicileRepresentation> representations = Lists.newLinkedList();
        for (AdvertDomicile advertDomicile : advertDomiciles) {
            representations.add(getAdvertDomicileRepresentation(advertDomicile));
        }
        return representations;
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
        return categories == null ? null : new AdvertCategoriesRepresentation().withIndustries(advertService.getAdvertIndustries(advert))
                .withFunctions(advertService.getAdvertFunctions(advert)).withThemes(advertService.getAdvertThemes(advert));
    }

    private AdvertTargetsRepresentation getAdvertTargetsRepresentation(Advert advert) {
        AdvertTargets targets = advertService.getAdvertTargets(advert);
        return targets == null ? null : new AdvertTargetsRepresentation().withCompetences(getAdvertCompetencesRepresentation(advert)).withInstitutions(
                getAdvertTargetsRepresentation(advert, AdvertInstitution.class))
                .withDepartments(getAdvertTargetsRepresentation(advert, AdvertDepartment.class))
                .withPrograms(getAdvertTargetsRepresentation(advert, AdvertProgram.class))
                .withSubjectAreas(getAdvertTargetsRepresentation(advert, AdvertSubjectArea.class));
    }

    private List<AdvertTargetRepresentation> getAdvertTargetsRepresentation(Advert advert, Class<? extends AdvertTarget<?>> targetClass) {
        List<AdvertTargetRepresentation> representations = Lists.newLinkedList();
        for (AdvertTarget<?> target : advertService.getAdvertTargets(advert, targetClass)) {
            representations.add(new AdvertTargetRepresentation().withId(target.getId()).withTitle(target.getTitle()).withImportance(target.getImportance()));
        }
        return representations;
    }

    private List<AdvertCompetenceRepresentation> getAdvertCompetencesRepresentation(Advert advert) {
        List<AdvertCompetenceRepresentation> representations = Lists.newLinkedList();
        for (AdvertTarget<?> target : advertService.getAdvertTargets(advert, AdvertCompetence.class)) {
            AdvertCompetence competence = (AdvertCompetence) target;
            representations.add(new AdvertCompetenceRepresentation().withId(competence.getId()).withTitle(competence.getTitle())
                    .withDescription(competence.getCompetence().getDescription()).withImportance(competence.getImportance()));
        }
        return representations;
    }

    private AdvertAddressRepresentation getAdvertAddressRepresentation(Advert advert) {
        AdvertAddress address = advert.getAddress();
        if (address != null) {
            AdvertAddressRepresentation representation = new AdvertAddressRepresentation()
                    .withDomicile(getAdvertDomicileRepresentation(address.getDomicile())).withAddressLine1(address.getAddressLine1())
                    .withAddressLine2(address.getAddressLine2()).withAddressTown(address.getAddressTown()).withAddressRegion(address.getAddressRegion())
                    .withAddressCode(address.getAddressCode()).withGoogleId(address.getGoogleId());

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

    private AdvertDomicileRepresentation getAdvertDomicileRepresentation(AdvertDomicile domicile) {
        return new AdvertDomicileRepresentation().withId(domicile.getId()).withName(domicile.getName()).withCurrency(domicile.getCurrency());
    }

}
