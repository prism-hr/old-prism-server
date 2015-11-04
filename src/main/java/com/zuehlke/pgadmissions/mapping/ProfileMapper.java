package com.zuehlke.pgadmissions.mapping;

import com.zuehlke.pgadmissions.domain.Domicile;
import com.zuehlke.pgadmissions.domain.address.Address;
import com.zuehlke.pgadmissions.domain.application.*;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.*;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAdditionalInformation;
import com.zuehlke.pgadmissions.rest.representation.address.AddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.profile.*;
import com.zuehlke.pgadmissions.rest.representation.resource.ResourceRelationInvitationRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileMapper {

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public <T extends ProfilePersonalDetail<?>> ProfilePersonalDetailRepresentation getPersonalDetailRepresentation(T personalDetail) {
        if (personalDetail != null) {
            Domicile nationality = personalDetail.getNationality();
            Domicile domicile = personalDetail.getDomicile();

            ProfilePersonalDetailRepresentation representation = new ProfilePersonalDetailRepresentation().withGender(personalDetail.getGender())
                    .withDateOfBirth(personalDetail.getAssociation().getUser().getUserAccount().getPersonalDetail().getDateOfBirth())
                    .withNationality(nationality == null ? null : nationality.getId()).withDomicile(domicile == null ? null : domicile.getId())
                    .withVisaRequired(personalDetail.getVisaRequired()).withPhone(personalDetail.getPhone()).withSkype(personalDetail.getSkype());

            if (personalDetail.getClass().equals(ApplicationPersonalDetail.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationPersonalDetail) personalDetail).getLastUpdatedTimestamp());
            }

            return representation;
        }
        return null;
    }

    public <T extends ProfileAddress<?>> ProfileAddressRepresentation getAddressRepresentation(T address) {
        if (address != null) {
            ProfileAddressRepresentation representation = new ProfileAddressRepresentation().withCurrentAddress(getAddressRepresentation(address.getCurrentAddress()))
                    .withContactAddress(getAddressRepresentation(address.getContactAddress()));

            if (address.getClass().equals(ApplicationAddress.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationAddress) address).getLastUpdatedTimestamp());
            }

            return representation;
        }
        return null;
    }

    public <T extends ProfileQualification<?>> List<ProfileQualificationRepresentation> getQualificationRepresentations(Set<T> qualifications) {
        return qualifications.stream()
                .map(this::getQualificationRepresentation)
                .sorted((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileEmploymentPosition<?>> List<ProfileEmploymentPositionRepresentation> getEmploymentPositionRepresentations(Set<T> employmentPositions) {
        return employmentPositions.stream()
                .map(this::getApplicationEmploymentPositionRepresentation)
                .sorted((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileReferee<?>> List<ProfileRefereeRepresentation> getRefereeRepresentations(Set<T> referees) {
        return referees.stream()
                .map(this::getRefereeRepresentation)
                .sorted((o1, o2) -> o1.getResource().getUser().getFullName().compareTo(o2.getResource().getUser().getFullName()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileDocument<?>> ProfileDocumentRepresentation getDocumentRepresentation(T document) {
        if (document != null) {
            ProfileDocumentRepresentation representation = new ProfileDocumentRepresentation();
            representation.setPersonalSummary(document.getPersonalSummary());

            Document cv = document.getCv();
            representation.setCv(cv == null ? null : documentMapper.getDocumentRepresentation(cv));

            if (document.getClass().equals(ApplicationDocument.class)) {
                Document coveringLetter = ((ApplicationDocument) document).getCoveringLetter();
                representation.setCoveringLetter(coveringLetter == null ? null : documentMapper.getDocumentRepresentation(coveringLetter));
                representation.setLinkedinProfileUrl(document.getAssociation().getUser().getUserAccount().getLinkedinProfileUrl());
                representation.setLastUpdatedTimestamp(((ApplicationDocument) document).getLastUpdatedTimestamp());
            }

            return representation;
        }

        return null;
    }

    public <T extends ProfileAdditionalInformation<?>> ProfileAdditionalInformationRepresentation getAdditionalInformationRepresentation(T additionalInformation) {
        if (additionalInformation != null) {
            User user = userService.getCurrentUser();
            if (additionalInformation.getClass().equals(ApplicationAdditionalInformation.class)
                    && applicationService.isCanViewEqualOpportunitiesData(((ApplicationAdditionalInformation) additionalInformation).getAssociation(), user)) {
                return new ProfileAdditionalInformationRepresentation().withRequirements(additionalInformation.getRequirements())
                        .withConvictions(additionalInformation.getConvictions())
                        .withLastUpdatedTimestamp(((ApplicationAdditionalInformation) additionalInformation).getLastUpdatedTimestamp());
            } else if (additionalInformation.getClass().equals(UserAdditionalInformation.class) && user.equals(additionalInformation.getAssociation().getUser())) {
                return new ProfileAdditionalInformationRepresentation().withRequirements(additionalInformation.getRequirements())
                        .withConvictions(additionalInformation.getConvictions());
            }
            return null;
        }

        return null;
    }

    public AddressRepresentation getAddressRepresentation(Address address) {
        AddressRepresentation representation = addressMapper.transform(address, AddressRepresentation.class);
        Domicile domicile = address.getDomicile();
        representation.setDomicile(domicile == null ? null : domicile.getId());
        return representation;
    }

    private <T extends ProfileQualification<?>> ProfileQualificationRepresentation getQualificationRepresentation(T qualification) {
        Document document = qualification.getDocument();

        Integer startYear = qualification.getStartYear();
        LocalDate startDate = startYear == null ? null : new LocalDate(startYear, qualification.getStartMonth(), 1);

        Integer awardYear = qualification.getAwardYear();
        LocalDate awardDate = awardYear == null ? null : new LocalDate(awardYear, qualification.getAwardMonth(), 1);

        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(qualification.getAdvert().getResource()));

        if (qualification.getUser() != null) {
            relation.setUser(userMapper.getUserRepresentationSimple(qualification.getUser()));
        }

        ProfileQualificationRepresentation representation = new ProfileQualificationRepresentation().withId(qualification.getId()).withResource(relation)
                .withGrade(qualification.getGrade()).withStartDate(startDate).withAwardDate(awardDate).withCompleted(qualification.getCompleted())
                .withDocumentRepresentation(document == null ? null : documentMapper.getDocumentRepresentation(document));

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationQualification) qualification).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileEmploymentPosition<?>> ProfileEmploymentPositionRepresentation getApplicationEmploymentPositionRepresentation(T employmentPosition) {
        Integer startYear = employmentPosition.getStartYear();
        LocalDate startDate = startYear == null ? null : new LocalDate(startYear, employmentPosition.getStartMonth(), 1);

        Integer endYear = employmentPosition.getEndYear();
        LocalDate endDate = endYear == null ? null : new LocalDate(endYear, employmentPosition.getEndMonth(), 1);

        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(employmentPosition.getAdvert().getResource()));
        if (employmentPosition.getUser() != null) {
            relation.setUser(userMapper.getUserRepresentationSimple(employmentPosition.getUser()));
        }

        ProfileEmploymentPositionRepresentation representation = new ProfileEmploymentPositionRepresentation().withId(employmentPosition.getId())
                .withResource(relation).withCurrent(employmentPosition.getCurrent()).withStartDate(startDate).withEndDate(endDate);

        if (employmentPosition.getClass().equals(ApplicationEmploymentPosition.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationEmploymentPosition) employmentPosition).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileReferee<?>> ProfileRefereeRepresentation getRefereeRepresentation(T referee) {
        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(referee.getAdvert().getResource()));

        if (referee.getUser() != null) {
            relation.setUser(userMapper.getUserRepresentationSimple(referee.getUser()));
        }

        ProfileRefereeRepresentation representation = new ProfileRefereeRepresentation().withId(referee.getId())
                .withResource(relation).withPhone(referee.getPhone()).withSkype(referee.getSkype());

        if (referee.getClass().equals(ApplicationReferee.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationReferee) referee).getLastUpdatedTimestamp());

        }

        return representation;
    }

}
