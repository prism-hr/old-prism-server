package uk.co.alumeni.prism.domain.advert;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import jersey.repackaged.com.google.common.base.Objects;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import uk.co.alumeni.prism.domain.Invitation;
import uk.co.alumeni.prism.domain.InvitationEntity;
import uk.co.alumeni.prism.domain.activity.Activity;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismPartnershipState;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.domain.user.UserAssignment;
import uk.co.alumeni.prism.workflow.user.AdvertConnectionReassignmentProcessor;

@Entity
@Table(name = "advert_target", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "advert_id", "advert_user_id", "target_advert_id", "target_advert_user_id", "accept_advert_id", "accept_advert_user_id" }) })
public class AdvertTarget extends AdvertAttribute implements Activity, UserAssignment<AdvertConnectionReassignmentProcessor>, InvitationEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "advert_user_id")
    private User advertUser;

    @Column(name = "advert_severed", nullable = false)
    private Boolean advertSevered;

    @ManyToOne
    @JoinColumn(name = "target_advert_id", nullable = false)
    private Advert targetAdvert;

    @ManyToOne
    @JoinColumn(name = "target_advert_user_id")
    private User targetAdvertUser;

    @Column(name = "target_advert_severed", nullable = false)
    private Boolean targetAdvertSevered;

    @ManyToOne
    @JoinColumn(name = "accept_advert_id", nullable = false)
    private Advert acceptAdvert;

    @ManyToOne
    @JoinColumn(name = "accept_advert_user_id")
    private User acceptAdvertUser;

    @ManyToOne
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Enumerated(EnumType.STRING)
    @Column(name = "partnership_state", nullable = false)
    private PrismPartnershipState partnershipState;

    @Column(name = "created_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdTimestamp;
    
    @Column(name = "accepted_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime acceptedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public User getAdvertUser() {
        return advertUser;
    }

    public void setAdvertUser(User advertUser) {
        this.advertUser = advertUser;
    }

    public Boolean getAdvertSevered() {
        return advertSevered;
    }

    public void setAdvertSevered(Boolean advertSevered) {
        this.advertSevered = advertSevered;
    }

    public Advert getTargetAdvert() {
        return targetAdvert;
    }

    public void setTargetAdvert(Advert targetAdvert) {
        this.targetAdvert = targetAdvert;
    }

    public User getTargetAdvertUser() {
        return targetAdvertUser;
    }

    public void setTargetAdvertUser(User targetAdvertUser) {
        this.targetAdvertUser = targetAdvertUser;
    }

    public Boolean getTargetAdvertSevered() {
        return targetAdvertSevered;
    }

    public void setTargetAdvertSevered(Boolean targetAdvertSevered) {
        this.targetAdvertSevered = targetAdvertSevered;
    }

    public Advert getAcceptAdvert() {
        return acceptAdvert;
    }

    public void setAcceptAdvert(Advert acceptAdvert) {
        this.acceptAdvert = acceptAdvert;
    }

    public User getAcceptAdvertUser() {
        return acceptAdvertUser;
    }

    public void setAcceptAdvertUser(User acceptAdvertUser) {
        this.acceptAdvertUser = acceptAdvertUser;
    }

    @Override
    public Invitation getInvitation() {
        return invitation;
    }

    @Override
    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public PrismPartnershipState getPartnershipState() {
        return partnershipState;
    }

    public void setPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
    }

    public DateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public DateTime getAcceptedTimestamp() {
        return acceptedTimestamp;
    }

    public void setAcceptedTimestamp(DateTime acceptedTimestamp) {
        this.acceptedTimestamp = acceptedTimestamp;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public AdvertTarget withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertTarget withAdvertUser(User advertUser) {
        this.advertUser = advertUser;
        return this;
    }

    public AdvertTarget withAdvertSevered(Boolean advertSevered) {
        this.advertSevered = advertSevered;
        return this;
    }

    public AdvertTarget withTargetAdvert(Advert targetAdvert) {
        this.targetAdvert = targetAdvert;
        return this;
    }

    public AdvertTarget withTargetAdvertUser(User targetAdvertUser) {
        this.targetAdvertUser = targetAdvertUser;
        return this;
    }

    public AdvertTarget withTargetAdvertSevered(Boolean targetAdvertSevered) {
        this.targetAdvertSevered = targetAdvertSevered;
        return this;
    }

    public AdvertTarget withAcceptAdvert(Advert acceptAdvert) {
        this.acceptAdvert = acceptAdvert;
        return this;
    }

    public AdvertTarget withAcceptAdvertUser(User acceptAdvertUser) {
        this.acceptAdvertUser = acceptAdvertUser;
        return this;
    }

    public AdvertTarget withInvitation(Invitation invitation) {
        this.invitation = invitation;
        return this;
    }
    
    public AdvertTarget withPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
        return this;
    }
    
    public AdvertTarget withCreatedTimestamp(DateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
        return this;
    }

    public Advert getOtherAdvert() {
        return Objects.equal(acceptAdvert.getId(), advert.getId()) ? targetAdvert : advert;
    }

    public User getOtherUser() {
        return Objects.equal(acceptAdvert.getId(), advert.getId()) ? targetAdvertUser : advertUser;
    }

    @Override
    public Class<AdvertConnectionReassignmentProcessor> getUserReassignmentProcessor() {
        return AdvertConnectionReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("advertUser", advertUser).addProperty("targetAdvert", targetAdvert).addProperty("targetAdvertUser", targetAdvertUser)
                .addProperty("acceptAdvert", acceptAdvert).addProperty("acceptAdvertUser", acceptAdvertUser);
    }

}
