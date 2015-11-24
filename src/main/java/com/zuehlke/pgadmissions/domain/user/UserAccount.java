package com.zuehlke.pgadmissions.domain.user;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.Activity;
import com.zuehlke.pgadmissions.domain.document.Document;
import com.zuehlke.pgadmissions.domain.profile.ProfileEntity;
import com.zuehlke.pgadmissions.domain.resource.ResourceListFilter;
import com.zuehlke.pgadmissions.domain.workflow.Scope;

@Entity
@Table(name = "user_account")
public class UserAccount
        implements Activity, ProfileEntity<UserPersonalDetail, UserAddress, UserQualification, UserEmploymentPosition, UserReferee, UserDocument, UserAdditionalInformation> {

    @Id
    @GeneratedValue
    private Integer id;

    @OneToOne(mappedBy = "userAccount")
    private User user;

    @Column(name = "password")
    private String password;

    @Column(name = "temporary_password")
    private String temporaryPassword;

    @Column(name = "temporary_password_expiry_timestamp")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime temporaryPasswordExpiryTimestamp;

    @Column(name = "send_activity_notification", nullable = false)
    private Boolean sendActivityNotification;

    @Column(name = "linkedin_id")
    private String linkedinId;

    @Column(name = "linkedin_profile_id")
    private String linkedinProfileUrl;

    @Column(name = "linkedin_image_id")
    private String linkedinImageUrl;

    @OneToOne
    @JoinColumn(name = "portrait_image_id")
    private Document portraitImage;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_personal_detail_id", unique = true)
    private UserPersonalDetail personalDetail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_address_id", unique = true)
    private UserAddress address;

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", nullable = false)
    private Set<UserQualification> qualifications = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", nullable = false)
    private Set<UserEmploymentPosition> employmentPositions = Sets.newHashSet();

    @OrderBy(clause = "id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_account_id", nullable = false)
    private Set<UserReferee> referees = Sets.newHashSet();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_document_id", unique = true)
    private UserDocument document;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_additional_information_id", unique = true)
    private UserAdditionalInformation additionalInformation;

    @Column(name = "shared", nullable = false)
    private Boolean shared;

    @Column(name = "updated_timestamp", nullable = false)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedTimestamp;

    @Column(name = "sequence_identifier", unique = true)
    private String sequenceIdentifier;

    @OneToMany(mappedBy = "userAccount")
    @MapKeyJoinColumn(name = "scope_id")
    private Map<Scope, ResourceListFilter> filters = Maps.newHashMap();

    @OneToMany(mappedBy = "userAccount")
    private Set<UserAccountUpdate> updates = Sets.newHashSet();

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTemporaryPassword() {
        return temporaryPassword;
    }

    public void setTemporaryPassword(String temporaryPassword) {
        this.temporaryPassword = temporaryPassword;
    }

    public DateTime getTemporaryPasswordExpiryTimestamp() {
        return temporaryPasswordExpiryTimestamp;
    }

    public void setTemporaryPasswordExpiryTimestamp(DateTime temporaryPasswordExpiryTimestamp) {
        this.temporaryPasswordExpiryTimestamp = temporaryPasswordExpiryTimestamp;
    }

    public Boolean getSendActivityNotification() {
        return sendActivityNotification;
    }

    public void setSendActivityNotification(Boolean sendActivityNotification) {
        this.sendActivityNotification = sendActivityNotification;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public void setLinkedinId(String linkedinId) {
        this.linkedinId = linkedinId;
    }

    public String getLinkedinProfileUrl() {
        return linkedinProfileUrl;
    }

    public void setLinkedinProfileUrl(String linkedinProfileUrl) {
        this.linkedinProfileUrl = linkedinProfileUrl;
    }

    public String getLinkedinImageUrl() {
        return linkedinImageUrl;
    }

    public void setLinkedinImageUrl(String linkedinImageUrl) {
        this.linkedinImageUrl = linkedinImageUrl;
    }

    public Document getPortraitImage() {
        return portraitImage;
    }

    public void setPortraitImage(Document portraitImage) {
        this.portraitImage = portraitImage;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public UserPersonalDetail getPersonalDetail() {
        return personalDetail;
    }

    @Override
    public void setPersonalDetail(UserPersonalDetail personalDetail) {
        this.personalDetail = personalDetail;
    }

    @Override
    public UserAddress getAddress() {
        return address;
    }

    @Override
    public void setAddress(UserAddress address) {
        this.address = address;
    }

    @Override
    public Set<UserQualification> getQualifications() {
        return qualifications;
    }

    @Override
    public Set<UserEmploymentPosition> getEmploymentPositions() {
        return employmentPositions;
    }

    @Override
    public Set<UserReferee> getReferees() {
        return referees;
    }

    @Override
    public UserDocument getDocument() {
        return document;
    }

    @Override
    public void setDocument(UserDocument document) {
        this.document = document;
    }

    @Override
    public UserAdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public void setAdditionalInformation(UserAdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public Boolean getShared() {
        return shared;
    }

    @Override
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public DateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getSequenceIdentifier() {
        return sequenceIdentifier;
    }

    @Override
    public void setSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
    }

    public Map<Scope, ResourceListFilter> getFilters() {
        return filters;
    }

    public Set<UserAccountUpdate> getUpdates() {
        return updates;
    }

    public UserAccount withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserAccount withSendActivityNotification(Boolean sendActivityNotification) {
        this.sendActivityNotification = sendActivityNotification;
        return this;
    }

    public UserAccount withEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserAccount withShared(Boolean shared) {
        this.shared = shared;
        return this;
    }

    public UserAccount withUpdatedTimestamp(DateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
        return this;
    }

    public UserAccount withSequenceIdentifier(String sequenceIdentifier) {
        this.sequenceIdentifier = sequenceIdentifier;
        return this;
    }

}
