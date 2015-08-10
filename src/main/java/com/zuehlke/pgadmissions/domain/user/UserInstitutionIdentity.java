package com.zuehlke.pgadmissions.domain.user;

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

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserInstitutionIdentity;
import com.zuehlke.pgadmissions.domain.resource.Institution;
import com.zuehlke.pgadmissions.workflow.user.UserInstitutionIdentityReassignmentProcessor;

@Entity
@Table(name = "user_institution_identity", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "institution_id", "user_identity_type" }) })
public class UserInstitutionIdentity implements UniqueEntity, UserAssignment<UserInstitutionIdentityReassignmentProcessor> {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "user_identity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismUserInstitutionIdentity identityType;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public PrismUserInstitutionIdentity getIdentityType() {
        return identityType;
    }

    public void setIdentityType(PrismUserInstitutionIdentity identityType) {
        this.identityType = identityType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public UserInstitutionIdentity withUser(User user) {
        this.user = user;
        return this;
    }

    public UserInstitutionIdentity withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public UserInstitutionIdentity withIdentityType(PrismUserInstitutionIdentity identityType) {
        this.identityType = identityType;
        return this;
    }

    public UserInstitutionIdentity withIdentitier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public Class<UserInstitutionIdentityReassignmentProcessor> getUserReassignmentProcessor() {
        return UserInstitutionIdentityReassignmentProcessor.class;
    }
    
    @Override
    public boolean isResourceUserAssignmentProperty() {
        return false;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("user", user).addProperty("institution", institution).addProperty("identityType", identityType);
    }

}
