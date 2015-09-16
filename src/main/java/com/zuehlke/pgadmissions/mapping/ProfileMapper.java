package com.zuehlke.pgadmissions.mapping;

import static com.zuehlke.pgadmissions.domain.definitions.PrismOauthProvider.LINKEDIN;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.application.ApplicationAdditionalInformation;
import com.zuehlke.pgadmissions.domain.application.ApplicationAddress;
import com.zuehlke.pgadmissions.domain.application.ApplicationDocument;
import com.zuehlke.pgadmissions.domain.application.ApplicationEmploymentPosition;
import com.zuehlke.pgadmissions.domain.application.ApplicationPersonalDetail;
import com.zuehlke.pgadmissions.domain.application.ApplicationQualification;
import com.zuehlke.pgadmissions.domain.application.ApplicationReferee;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileAdditionalInformation;
import com.zuehlke.pgadmissions.domain.profile.ProfileAddress;
import com.zuehlke.pgadmissions.domain.profile.ProfileDocument;
import com.zuehlke.pgadmissions.domain.profile.ProfileEmploymentPosition;
import com.zuehlke.pgadmissions.domain.profile.ProfilePersonalDetail;
import com.zuehlke.pgadmissions.domain.profile.ProfileQualification;
import com.zuehlke.pgadmissions.domain.profile.ProfileReferee;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAdditionalInformation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileAdditionalInformationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileDocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileEmploymentPositionRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfilePersonalDetailRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileQualificationRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ProfileRefereeRepresentation;
import com.zuehlke.pgadmissions.services.ApplicationService;
import com.zuehlke.pgadmissions.services.UserService;

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
    private ImportedEntityMapper importedEntityMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public <T extends ProfilePersonalDetail<?>> ProfilePersonalDetailRepresentation getPersonalDetailRepresentation(T personalDetail) {
        if (personalDetail != null) {
            ProfilePersonalDetailRepresentation representation = new ProfilePersonalDetailRepresentation()
                    .withTitle(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getTitle()))
                    .withGender(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getGender()))
                    .withDateOfBirth(personalDetail.getAssociation().getUser().getUserAccount().getPersonalDetail().getDateOfBirth())
                    .withFirstNationality(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getNationality()))
                    .withDomicile(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getDomicile()))
                    .withVisaRequired(personalDetail.getVisaRequired()).withPhone(personalDetail.getPhone()).withSkype(personalDetail.getSkype())
                    .withEthnicity(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getEthnicity()))
                    .withDisability(importedEntityMapper.getImportedEntityRepresentation(personalDetail.getDisability()));

            if (personalDetail.getClass().equals(ApplicationPersonalDetail.class)) {
                representation.setAgeRange(importedEntityMapper.getImportedEntityRepresentation(((ApplicationPersonalDetail) personalDetail).getAgeRange()));
                representation.setLastUpdatedTimestamp(((ApplicationPersonalDetail) personalDetail).getLastUpdatedTimestamp());
            }

            return representation;
        }
        return null;
    }

    public <T extends ProfileAddress<?>> ProfileAddressRepresentation getAddressRepresentation(T address) {
        if (address != null) {
            ProfileAddressRepresentation representation = new ProfileAddressRepresentation().withCurrentAddress(
                    addressMapper.getAddressApplicationRepresentation(address.getCurrentAddress())).withContactAddress(
                            addressMapper.getAddressApplicationRepresentation(address.getContactAddress()));

            if (address.getClass().equals(ApplicationAddress.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationAddress) address).getLastUpdatedTimestamp());
            }
        }
        return null;
    }

    public <T extends ProfileQualification<?>> List<ProfileQualificationRepresentation> getQualificationRepresentations(Set<T> qualifications) {
        return qualifications.stream().map(q -> getQualificationRepresentation(q)).collect(Collectors.toList());
    }

    public <T extends ProfileEmploymentPosition<?>> List<ProfileEmploymentPositionRepresentation> getEmploymentPositionRepresentations(Set<T> employmentPositions) {
        return employmentPositions.stream().map(ep -> getApplicationEmploymentPositionRepresentation(ep)).collect(Collectors.toList());
    }

    public <T extends ProfileReferee<?>> List<ProfileRefereeRepresentation> getRefereeRepresentations(Set<T> referees) {
        return referees.stream().map(r -> getRefereeRepresentation(r)).collect(Collectors.toList());
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
                representation.setLinkedinProfileUrl(userService.getOauthProfileUrl(document.getAssociation().getUser(), LINKEDIN));
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
                return new ProfileAdditionalInformationRepresentation().withConvictionsText(additionalInformation.getConvictionsText())
                        .withLastUpdatedTimestamp(((ApplicationAdditionalInformation) additionalInformation).getLastUpdatedTimestamp());
            } else if (additionalInformation.getClass().equals(UserAdditionalInformation.class) && user.equals(additionalInformation.getAssociation().getUser())) {
                return new ProfileAdditionalInformationRepresentation().withConvictionsText(additionalInformation.getConvictionsText());
            }
            return null;
        }

        return null;
    }

    private <T extends ProfileQualification<?>> ProfileQualificationRepresentation getQualificationRepresentation(T qualification) {
        Document document = qualification.getDocument();
        ProfileQualificationRepresentation representation = new ProfileQualificationRepresentation().withId(qualification.getId())
                .withProgram(resourceMapper.getResourceRepresentationActivity(qualification.getAdvert().getResource()))
                .withStartYear(qualification.getStartYear()).withStartMonth(qualification.getStartMonth())
                .withAwardYear(qualification.getAwardYear()).withAwardMonth(qualification.getAwardMonth())
                .withCompleted(qualification.getCompleted()).withDocumentRepresentation(document == null ? null : documentMapper.getDocumentRepresentation(document));

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationQualification) qualification).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileEmploymentPosition<?>> ProfileEmploymentPositionRepresentation getApplicationEmploymentPositionRepresentation(T employmentPosition) {
        ProfileEmploymentPositionRepresentation representation = new ProfileEmploymentPositionRepresentation().withStartYear(employmentPosition.getStartYear())
                .withStartMonth(employmentPosition.getStartMonth()).withEndYear(employmentPosition.getEndYear())
                .withEndMonth(employmentPosition.getEndMonth()).withCurrent(employmentPosition.getCurrent());

        if (employmentPosition.getClass().equals(ApplicationEmploymentPosition.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationEmploymentPosition) employmentPosition).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileReferee<?>> ProfileRefereeRepresentation getRefereeRepresentation(T referee) {
        ProfileRefereeRepresentation representation = new ProfileRefereeRepresentation().withId(referee.getId())
                .withUser(userMapper.getUserRepresentationSimple(referee.getUser()))
                .withResource(resourceMapper.getResourceRepresentationActivity(referee.getAdvert().getResource()))
                .withPhone(referee.getPhone()).withSkype(referee.getSkype());

        if (referee.getClass().equals(ApplicationReferee.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationReferee) referee).getLastUpdatedTimestamp());

        }

        return representation;
    }

}
