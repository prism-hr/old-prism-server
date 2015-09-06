package com.zuehlke.pgadmissions.domain.advert;

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

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismPartnershipState;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAssignment;
import com.zuehlke.pgadmissions.workflow.user.AdvertTargetAdvertReassignmentProcessor;

@Entity
@Table(name = "advert_target_advert", uniqueConstraints = { @UniqueConstraint(columnNames = { "advert_id", "target_advert_id" }) })
public class AdvertTargetAdvert extends AdvertTarget<Advert> implements UserAssignment<AdvertTargetAdvertReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "target_advert_id", nullable = false)
    private Advert value;

    @ManyToOne
    @JoinColumn(name = "value_user_id")
    private User valueUser;

    @Column(name = "selected", nullable = false)
    private Boolean selected;

    @Column(name = "partnership_state")
    @Enumerated(EnumType.STRING)
    private PrismPartnershipState partnershipState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Advert getAdvert() {
        return advert;
    }

    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Advert getValue() {
        return value;
    }

    public void setValue(Advert value) {
        this.value = value;
    }

    public User getValueUser() {
        return valueUser;
    }

    public void setValueUser(User valueUser) {
        this.valueUser = valueUser;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public PrismPartnershipState getPartnershipState() {
        return partnershipState;
    }

    public void setPartnershipState(PrismPartnershipState partnershipState) {
        this.partnershipState = partnershipState;
    }

    @Override
    public Integer getValueId() {
        return value.getId();
    }

    @Override
    public String getName() {
        return value.getName();
    }

    public AdvertTargetAdvert withAdvert(Advert advert) {
        this.advert = advert;
        return this;
    }

    public AdvertTargetAdvert withValue(Advert value) {
        this.value = value;
        return this;
    }

    public AdvertTargetAdvert withSelected(Boolean selected) {
        this.selected = selected;
        return this;
    }
    
    @Override
    public Class<AdvertTargetAdvertReassignmentProcessor> getUserReassignmentProcessor() {
        return AdvertTargetAdvertReassignmentProcessor.class;
    }

    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

}
