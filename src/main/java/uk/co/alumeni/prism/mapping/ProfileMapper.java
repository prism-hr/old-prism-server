package uk.co.alumeni.prism.mapping;

import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.doubleToBigDecimal;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.longToInteger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.co.alumeni.prism.domain.Domicile;
import uk.co.alumeni.prism.domain.address.Address;
import uk.co.alumeni.prism.domain.application.ApplicationAdditionalInformation;
import uk.co.alumeni.prism.domain.application.ApplicationAddress;
import uk.co.alumeni.prism.domain.application.ApplicationDocument;
import uk.co.alumeni.prism.domain.application.ApplicationEmploymentPosition;
import uk.co.alumeni.prism.domain.application.ApplicationPersonalDetail;
import uk.co.alumeni.prism.domain.application.ApplicationQualification;
import uk.co.alumeni.prism.domain.application.ApplicationReferee;
import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.document.Document;
import uk.co.alumeni.prism.domain.profile.ProfileAdditionalInformation;
import uk.co.alumeni.prism.domain.profile.ProfileAddress;
import uk.co.alumeni.prism.domain.profile.ProfileAdvertRelationSection;
import uk.co.alumeni.prism.domain.profile.ProfileAward;
import uk.co.alumeni.prism.domain.profile.ProfileDocument;
import uk.co.alumeni.prism.domain.profile.ProfileEmploymentPosition;
import uk.co.alumeni.prism.domain.profile.ProfilePersonalDetail;
import uk.co.alumeni.prism.domain.profile.ProfileQualification;
import uk.co.alumeni.prism.domain.profile.ProfileReferee;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAccount;
import uk.co.alumeni.prism.domain.user.UserDocument;
import uk.co.alumeni.prism.domain.user.UserEmploymentPosition;
import uk.co.alumeni.prism.domain.user.UserQualification;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileAdditionalInformationRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileAddressRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileAwardRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileDocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileEmploymentPositionRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileListRowRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfilePersonalDetailRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileQualificationRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRefereeRepresentation;
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationSummary;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationInvitationRepresentation;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ProfileService;
import uk.co.alumeni.prism.services.UserService;

import com.google.common.collect.Lists;

@Service
@Transactional
public class ProfileMapper {

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private UserMapper userMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentService commentService;

    @Inject
    private ProfileService profileService;

    @Inject
    private UserService userService;

    public List<ProfileListRowRepresentation> getProfileListRowRepresentations(ProfileListFilterDTO filter) {
        User currentUser = userService.getCurrentUser();
        DateTime updatedBaseline = now().minusDays(1);
        List<ProfileListRowRepresentation> representations = Lists.newLinkedList();
        userService.getUserProfiles(filter, currentUser).forEach(user -> { //
                    Long applicationCount = user.getApplicationCount();
                    Long applicationRatingCount = user.getApplicationRatingCount();
                    BigDecimal applicationRatingAverage = user.getApplicationRatingAverage();
                    representations.add(new ProfileListRowRepresentation()
                            .withRaisesUpdateFlag(user.getUpdatedTimestamp().isAfter(updatedBaseline))
                            .withUser(userMapper.getUserRepresentationSimple(user, currentUser))
                            .withLinkedInProfileUrl(user.getLinkedInProfileUrl())
                            .withApplicationCount(applicationCount == null ? null : applicationCount.intValue())
                            .withApplicationRatingCount(applicationRatingCount == null ? null : applicationRatingCount.intValue())
                            .withApplicationRatingAverage(
                                    applicationRatingAverage == null ? null : applicationRatingAverage.setScale(RATING_PRECISION, HALF_UP))
                            .withUpdatedTimestamp(user.getUpdatedTimestamp())
                            .withSequenceIdentifier(user.getSequenceIdentifier()));
                });
        return representations;
    }

    public <T extends ProfilePersonalDetail<?>> ProfilePersonalDetailRepresentation getPersonalDetailRepresentation(T personalDetail,
            boolean viewEqualOpportunities) {
        if (personalDetail != null) {
            Domicile nationality = personalDetail.getNationality();
            Domicile domicile = personalDetail.getDomicile();

            ProfilePersonalDetailRepresentation representation = new ProfilePersonalDetailRepresentation().withGender(personalDetail.getGender())
                    .withDateOfBirth(personalDetail.getAssociation().getUser().getUserAccount().getPersonalDetail().getDateOfBirth())
                    .withNationality(nationality == null ? null : nationality.getId()).withDomicile(domicile == null ? null : domicile.getId())
                    .withVisaRequired(personalDetail.getVisaRequired()).withPhone(personalDetail.getPhone()).withSkype(personalDetail.getSkype());

            if (viewEqualOpportunities) {
                representation.setEthnicity(personalDetail.getEthnicity());
                representation.setDisability(personalDetail.getDisability());
            }

            if (personalDetail.getClass().equals(ApplicationPersonalDetail.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationPersonalDetail) personalDetail).getLastUpdatedTimestamp());
            }

            return representation;
        }
        return null;
    }

    public <T extends ProfileAddress<?>> ProfileAddressRepresentation getAddressRepresentation(T address) {
        if (address != null) {
            ProfileAddressRepresentation representation = new ProfileAddressRepresentation().withCurrentAddress(
                    getAddressRepresentation(address.getCurrentAddress()))
                    .withContactAddress(getAddressRepresentation(address.getContactAddress()));

            if (address.getClass().equals(ApplicationAddress.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationAddress) address).getLastUpdatedTimestamp());
            }

            return representation;
        }
        return null;
    }

    public <T extends ProfileQualification<?>> List<ProfileQualificationRepresentation> getQualificationRepresentations(Collection<T> qualifications,
            User currentUser) {
        return qualifications.stream()
                .map(qualification -> getQualificationRepresentation(qualification, currentUser))
                .sorted((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileAward<?>> List<ProfileAwardRepresentation> getAwardRepresentations(Collection<T> awards) {
        return awards.stream()
                .map(this::getAwardRepresentation)
                .sorted((o1, o2) -> o1.getAwardDate().compareTo(o2.getAwardDate()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileEmploymentPosition<?>> List<ProfileEmploymentPositionRepresentation> getEmploymentPositionRepresentations(
            Collection<T> employmentPositions, User currentUser) {
        return employmentPositions.stream()
                .map(employmentPosition -> getEmploymentPositionRepresentation(employmentPosition, currentUser))
                .sorted((o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()))
                .collect(Collectors.toList());
    }

    public <T extends ProfileReferee<?>> List<ProfileRefereeRepresentation> getRefereeRepresentations(Collection<T> referees, User currentUser) {
        return referees.stream()
                .map(referee -> getRefereeRepresentation(referee, currentUser))
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

    public <T extends ProfileAdditionalInformation<?>> ProfileAdditionalInformationRepresentation getAdditionalInformationRepresentation(
            T additionalInformation,
            boolean viewEqualOpportunities) {
        if (additionalInformation != null && viewEqualOpportunities) {
            ProfileAdditionalInformationRepresentation representation = new ProfileAdditionalInformationRepresentation().withRequirements(
                    additionalInformation.getRequirements())
                    .withConvictions(additionalInformation.getConvictions());

            if (additionalInformation.getClass().equals(ApplicationAdditionalInformation.class)) {
                representation.setLastUpdatedTimestamp(((ApplicationAdditionalInformation) additionalInformation).getLastUpdatedTimestamp());
            }

            return representation;
        }

        return null;
    }

    public AddressRepresentation getAddressRepresentation(Address address) {
        AddressRepresentation representation = addressMapper.transform(address, AddressRepresentation.class);
        Domicile domicile = address.getDomicile();
        representation.setDomicile(domicile == null ? null : domicile.getId());
        return representation;
    }

    public ProfileRepresentationSummary getProfileRepresentationSummary(Integer userId) {
        User user = userService.getById(userId);
        User currentUser = userService.getCurrentUser();
        if (user.equals(currentUser) || userService.getUserProfiles(new ProfileListFilterDTO().withUserId(userId), currentUser) != null) {
            ProfileRepresentationSummary representation = new ProfileRepresentationSummary();
            representation.setUser(userMapper.getUserRepresentationSimple(user, currentUser));
            representation.setCreatedTimestamp(userService.getUserCreatedTimestamp(user));

            ResourceRatingSummaryDTO ratingSummary = applicationService.getApplicationRatingSummary(user);
            representation.setApplicationCount(longToInteger(ratingSummary.getResourceCount()));
            representation.setApplicationRatingCount(longToInteger(ratingSummary.getRatingCount()));
            representation.setApplicationRatingAverage(doubleToBigDecimal(ratingSummary.getRatingAverage(), RATING_PRECISION));

            List<Comment> ratingComments = commentService.getRatingComments(APPLICATION, user);
            representation.setActionSummaries(commentMapper.getRatingCommentSummaryRepresentations(ratingComments));

            UserAccount userAccount = user.getUserAccount();
            representation.setRecentQualifications(getQualificationRepresentations(
                    profileService.getRecentQualifications(userAccount, UserQualification.class), currentUser));
            representation.setRecentEmploymentPositions(getEmploymentPositionRepresentations(
                    profileService.getRecentEmploymentPositions(userAccount, UserEmploymentPosition.class), currentUser));

            UserDocument document = userAccount.getDocument();
            if (document != null) {
                representation.setCv(documentMapper.getDocumentRepresentation(document.getCv()));
                representation.setPersonalSummary(document.getPersonalSummary());
            }
        }

        return null;
    }

    private <T extends ProfileQualification<?>> ProfileQualificationRepresentation getQualificationRepresentation(T qualification, User currentUser) {
        Document document = qualification.getDocument();

        Integer startYear = qualification.getStartYear();
        LocalDate startDate = startYear == null ? null : new LocalDate(startYear, qualification.getStartMonth(), 1);

        Integer awardYear = qualification.getAwardYear();
        LocalDate awardDate = awardYear == null ? null : new LocalDate(awardYear, qualification.getAwardMonth(), 1);

        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(qualification.getAdvert().getResource(), currentUser));

        setUserRepresentation(qualification, relation, currentUser);
        ProfileQualificationRepresentation representation = new ProfileQualificationRepresentation().withId(qualification.getId()).withResource(relation)
                .withGrade(qualification.getGrade()).withStartDate(startDate).withAwardDate(awardDate).withCompleted(qualification.getCompleted())
                .withDocumentRepresentation(document == null ? null : documentMapper.getDocumentRepresentation(document));

        if (qualification.getClass().equals(ApplicationQualification.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationQualification) qualification).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileAward<?>> ProfileAwardRepresentation getAwardRepresentation(T award) {
        Integer awardYear = award.getAwardYear();
        return new ProfileAwardRepresentation().withId(award.getId()).withName(award.getName()).withDescription(award.getDescription())
                .withAwardDate(awardYear == null ? null : new LocalDate(awardYear, award.getAwardMonth(), 1));
    }

    private <T extends ProfileEmploymentPosition<?>> ProfileEmploymentPositionRepresentation getEmploymentPositionRepresentation(T employmentPosition,
            User currentUser) {
        Integer startYear = employmentPosition.getStartYear();
        LocalDate startDate = startYear == null ? null : new LocalDate(startYear, employmentPosition.getStartMonth(), 1);

        Integer endYear = employmentPosition.getEndYear();
        LocalDate endDate = endYear == null ? null : new LocalDate(endYear, employmentPosition.getEndMonth(), 1);

        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(employmentPosition.getAdvert().getResource(), currentUser));

        setUserRepresentation(employmentPosition, relation, currentUser);
        ProfileEmploymentPositionRepresentation representation = new ProfileEmploymentPositionRepresentation().withId(employmentPosition.getId())
                .withResource(relation).withCurrent(employmentPosition.getCurrent()).withStartDate(startDate).withEndDate(endDate);

        if (employmentPosition.getClass().equals(ApplicationEmploymentPosition.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationEmploymentPosition) employmentPosition).getLastUpdatedTimestamp());
        }

        return representation;
    }

    private <T extends ProfileReferee<?>> ProfileRefereeRepresentation getRefereeRepresentation(T referee, User currentUser) {
        ResourceRelationInvitationRepresentation relation = new ResourceRelationInvitationRepresentation()
                .withResource(resourceMapper.getResourceOpportunityRepresentationRelation(referee.getAdvert().getResource(), currentUser));

        setUserRepresentation(referee, relation, currentUser);
        ProfileRefereeRepresentation representation = new ProfileRefereeRepresentation().withId(referee.getId())
                .withResource(relation).withPhone(referee.getPhone()).withSkype(referee.getSkype());

        if (referee.getClass().equals(ApplicationReferee.class)) {
            representation.setLastUpdatedTimestamp(((ApplicationReferee) referee).getLastUpdatedTimestamp());

        }

        return representation;
    }

    private <T extends ProfileAdvertRelationSection<?>> void setUserRepresentation(T entity, ResourceRelationInvitationRepresentation representation,
            User currentUser) {
        User user = entity.getUser();
        if (user != null) {
            representation.setUser(userMapper.getUserRepresentationSimple(user, currentUser));
        }
    }

}
