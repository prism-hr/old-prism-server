package uk.co.alumeni.prism.mapping;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.math.RoundingMode.HALF_UP;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.joda.time.DateTime.now;
import static uk.co.alumeni.prism.PrismConstants.RATING_PRECISION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismRole.PrismRoleCategory.STUDENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.APPLICATION;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.DEPARTMENT;
import static uk.co.alumeni.prism.domain.definitions.workflow.PrismScope.INSTITUTION;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.doubleToBigDecimal;
import static uk.co.alumeni.prism.utils.PrismConversionUtils.longToInteger;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import uk.co.alumeni.prism.domain.definitions.workflow.PrismRole;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
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
import uk.co.alumeni.prism.dto.ActivityMessageCountDTO;
import uk.co.alumeni.prism.dto.ProfileListRowDTO;
import uk.co.alumeni.prism.dto.ResourceRatingSummaryDTO;
import uk.co.alumeni.prism.dto.UserOrganizationDTO;
import uk.co.alumeni.prism.exceptions.PrismForbiddenException;
import uk.co.alumeni.prism.rest.dto.profile.ProfileListFilterDTO;
import uk.co.alumeni.prism.rest.representation.ProfileRepresentationCandidate;
import uk.co.alumeni.prism.rest.representation.address.AddressRepresentation;
import uk.co.alumeni.prism.rest.representation.comment.CommentRepresentation;
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
import uk.co.alumeni.prism.rest.representation.profile.ProfileRepresentationUser;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRelationInvitationRepresentation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationRelation;
import uk.co.alumeni.prism.rest.representation.resource.ResourceRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.user.UserActivityRepresentation.ActivityRepresentation;
import uk.co.alumeni.prism.rest.representation.user.UserRepresentationSimple;
import uk.co.alumeni.prism.services.ApplicationService;
import uk.co.alumeni.prism.services.CommentService;
import uk.co.alumeni.prism.services.ProfileService;
import uk.co.alumeni.prism.services.ResourceService;
import uk.co.alumeni.prism.services.RoleService;
import uk.co.alumeni.prism.services.UserService;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedHashMultimap;

@Service
@Transactional
public class ProfileMapper {

    @Inject
    private AddressMapper addressMapper;

    @Inject
    private ApplicationService applicationService;

    @Inject
    private CommentMapper commentMapper;

    @Inject
    private CommentService commentService;

    @Inject
    private DocumentMapper documentMapper;

    @Inject
    private ProfileService profileService;

    @Inject
    private ResourceMapper resourceMapper;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RoleService roleService;

    @Inject
    private UserMapper userMapper;

    @Inject
    private UserService userService;

    public List<ProfileListRowRepresentation> getProfileListRowRepresentations(ProfileListFilterDTO filter, String lastSequenceIdentifier) {
        User currentUser = userService.getCurrentUser();
        List<ProfileListRowRepresentation> representations = newLinkedList();

        List<ProfileListRowDTO> profiles = userService.getUserProfiles(filter, currentUser, lastSequenceIdentifier);
        if (profiles.size() > 0) {
            DateTime baseline = now().minusDays(1);

            List<Integer> userIds = profiles.stream().map(p -> p.getUserId()).collect(toList());
            Map<Integer, Integer> readMessagesIndex = getMessageCountIndex(userService.getUserReadMessageCounts(userIds, currentUser));
            Map<Integer, Integer> unreadMessagesIndex = getMessageCountIndex(userService.getUserUnreadMessageCounts(userIds, currentUser));

            LinkedHashMultimap<Integer, String> userLocationIndex = userService.getUserLocations(userIds);
            HashMultimap<Integer, UserOrganizationDTO> userOrganizationIndex = userService.getUserOrganizations(userIds, STUDENT);

            HashMultimap<PrismScope, Integer> resourceIndex = HashMultimap.create();
            Integer maximumCompleteScore = userService.getMaximumUserAccountCompleteScore();
            profiles.forEach(profile -> {
                Integer userId = profile.getUserId();
                Set<String> locations = userLocationIndex.get(userId);

                Set<ResourceRepresentationRelation> userOrganizations = newTreeSet();
                for (UserOrganizationDTO userOrganizationDTO : userOrganizationIndex.get(userId)) {
                    Integer departmentId = userOrganizationDTO.getDepartmentId();

                    ResourceRepresentationRelation userOrganization;
                    if (departmentId == null) {
                        Integer institutionId = userOrganizationDTO.getInstitutionId();
                        userOrganization = new ResourceRepresentationRelation().withScope(INSTITUTION)
                                .withId(institutionId).withName(userOrganizationDTO.getInstitutionName())
                                .withLogoImage(documentMapper.getDocumentRepresentation(userOrganizationDTO.getInstitutionLogoImageId()));
                        resourceIndex.put(INSTITUTION, institutionId);
                    } else {
                        userOrganization = new ResourceRepresentationRelation().withScope(DEPARTMENT)
                                .withId(departmentId).withName(userOrganizationDTO.getDepartmentName())
                                .withInstitution(new ResourceRepresentationSimple().withScope(INSTITUTION)
                                        .withId(userOrganizationDTO.getInstitutionId()).withName(userOrganizationDTO.getInstitutionName())
                                        .withLogoImage(documentMapper.getDocumentRepresentation(userOrganizationDTO.getInstitutionLogoImageId())));
                        resourceIndex.put(DEPARTMENT, departmentId);
                    }

                    userOrganizations.add(userOrganization);
                }

                representations.add(new ProfileListRowRepresentation().withReadMessageCount(readMessagesIndex.get(userId))
                        .withUnreadMessageCount(unreadMessagesIndex.get(userId)).withRaisesUpdateFlag(profile.getUpdatedTimestamp().isAfter(baseline))
                        .withCompleteScore(getProfileCompleteScoreAsRatio(profile.getCompleteScore(), maximumCompleteScore))
                        .withUser(userMapper.getUserRepresentationSimple(profile, currentUser)).withLocations(newLinkedList(locations))
                        .withOrganizations(newLinkedList(userOrganizations)).withLinkedInProfileUrl(profile.getLinkedInProfileUrl())
                        .withApplicationCount(longToInteger(profile.getApplicationCount()))
                        .withApplicationRatingCount(longToInteger(profile.getApplicationRatingCount()))
                        .withApplicationRatingAverage(doubleToBigDecimal(profile.getApplicationRatingAverage(), RATING_PRECISION))
                        .withUpdatedTimestamp(profile.getUpdatedTimestamp()).withSequenceIdentifier(profile.getSequenceIdentifier()));
            });

            Set<Integer> departmentIds = resourceIndex.get(DEPARTMENT);
            LinkedHashMultimap<Integer, String> departmentLocations = LinkedHashMultimap.create();
            if (isNotEmpty(departmentIds)) {
                departmentLocations = resourceService.getResourceOrganizationLocations(DEPARTMENT, departmentIds);
            }

            Set<Integer> institutionIds = resourceIndex.get(INSTITUTION);
            LinkedHashMultimap<Integer, String> institutionLocations = LinkedHashMultimap.create();
            if (isNotEmpty(institutionIds)) {
                institutionLocations = resourceService.getResourceOrganizationLocations(INSTITUTION, institutionIds);
            }

            for (ProfileListRowRepresentation representation : representations) {
                if (isEmpty(representation.getLocations())) {
                    ResourceRepresentationSimple resource = representation.getOrganizations().iterator().next();
                    if (resource.getScope().equals(DEPARTMENT)) {
                        representation.setLocations(newLinkedList(departmentLocations.get(resource.getId())));
                    } else {
                        representation.setLocations(newLinkedList(institutionLocations.get(resource.getId())));
                    }
                }
            }
        }

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
        if (userService.checkUserCanViewUserProfile(user, currentUser)) {
            ProfileRepresentationSummary representation = new ProfileRepresentationSummary();
            representation.setUser(userMapper.getUserRepresentationSimple(user, currentUser));
            representation.setCreatedTimestamp(userService.getUserCreatedTimestamp(user));

            ResourceRatingSummaryDTO ratingSummary = applicationService.getApplicationRatingSummary(user);
            representation.setApplicationCount(longToInteger(ratingSummary.getResourceCount()));
            representation.setApplicationRatingCount(longToInteger(ratingSummary.getRatingCount()));
            representation.setApplicationRatingAverage(doubleToBigDecimal(ratingSummary.getRatingAverage(), RATING_PRECISION));

            List<PrismRole> creatableRoles = roleService.getCreatableRoles(APPLICATION);
            List<CommentRepresentation> ratingComments = commentService.getRatingComments(APPLICATION, user).stream()
                    .map(c -> commentMapper.getCommentRepresentationExtended(c, creatableRoles)).collect(toList());
            representation.setActionSummaries(commentMapper.getRatingCommentSummaryRepresentations(currentUser, APPLICATION, ratingComments));

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
            return representation;
        }

        throw new PrismForbiddenException("user does not have permission to access candidate data");
    }

    public ProfileRepresentationCandidate getProfileRepresentationCandidate(Integer userId) {
        User user = userService.getById(userId);
        User currentUser = userService.getCurrentUser();
        if (userService.checkUserCanViewUserProfile(user, currentUser)) {
            ProfileRepresentationUser profileRepresentation = getProfileRepresentationUser(user, currentUser);
            UserRepresentationSimple userRepresentation = userMapper.getUserRepresentationSimple(user, currentUser);

            return new ProfileRepresentationCandidate().withUser(userRepresentation).withProfile(profileRepresentation)
                    .withReadMessageCount(userService.getUserReadMessageCount(user, currentUser))
                    .withUnreadMessageCount(userService.getUserUnreadMessageCount(user, currentUser));
        }

        throw new PrismForbiddenException("user does not have permission to access candidate data");
    }

    public ProfileRepresentationUser getProfileRepresentationUser(User user, User currentUser) {
        UserAccount userAccount = user.getUserAccount();
        Integer maximumCompleteScore = userService.getMaximumUserAccountCompleteScore();
        ProfileRepresentationUser representation = new ProfileRepresentationUser()
                .withCompleteScore(getProfileCompleteScoreAsRatio(userAccount.getCompleteScore(), maximumCompleteScore))
                .withPersonalDetail(getPersonalDetailRepresentation(userAccount.getPersonalDetail(), true))
                .withAddress(getAddressRepresentation(userAccount.getAddress()))
                .withQualifications(getQualificationRepresentations(userAccount.getQualifications(), user))
                .withAwards(getAwardRepresentations(userAccount.getAwards()))
                .withEmploymentPositions(getEmploymentPositionRepresentations(userAccount.getEmploymentPositions(), user))
                .withReferees(getRefereeRepresentations(userAccount.getReferees(), user))
                .withDocument(getDocumentRepresentation(userAccount.getDocument()))
                .withAdditionalInformation(getAdditionalInformationRepresentation(userAccount.getAdditionalInformation(), true))
                .withShared(userAccount.getShared()).withUpdatedTimestamp(userAccount.getUpdatedTimestamp());

        if (equal(user, currentUser)) {
            representation.setReadMessageCount(userService.getUserReadMessageCount(user, currentUser));
            representation.setUnreadMessageCount(userService.getUserUnreadMessageCount(user, currentUser));
        }

        return representation;
    }

    public ActivityRepresentation getProfileActivityRepresentation(User user) {
        List<ProfileListRowDTO> profiles = userService.getUserProfiles(user);

        Integer count = profiles.size();
        if (count > 0) {
            Integer updateCount = 0;
            DateTime baseline = new DateTime().minusDays(1);
            for (ProfileListRowDTO profile : profiles) {
                if (profile.getUpdatedTimestamp().isAfter(baseline)) {
                    updateCount++;
                }
            }

            List<Integer> userIds = profiles.stream().map(p -> p.getUserId()).collect(toList());
            return new ActivityRepresentation().withCount(count).withUpdateCount(updateCount)
                    .withMessageCount(userService.getUserUnreadMessageCount(userIds, user).intValue());
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

    private Map<Integer, Integer> getMessageCountIndex(List<ActivityMessageCountDTO> messageCounts) {
        Map<Integer, Integer> readMessagesIndex = newHashMap();
        messageCounts.stream().forEach(umc -> readMessagesIndex.put(umc.getId(), umc.getMessageCount().intValue()));
        return readMessagesIndex;
    }

    private BigDecimal getProfileCompleteScoreAsRatio(Integer completeScore, Integer maximumCompleteScore) {
        return maximumCompleteScore.equals(0) ? new BigDecimal(0).setScale(RATING_PRECISION) : new BigDecimal(completeScore).divide(new BigDecimal(
                maximumCompleteScore), RATING_PRECISION, HALF_UP);
    }

}
