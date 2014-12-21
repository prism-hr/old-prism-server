package com.zuehlke.pgadmissions.domain.user;

import com.zuehlke.pgadmissions.domain.IUniqueEntity;
import com.zuehlke.pgadmissions.domain.definitions.PrismUserIdentity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.*;

@Entity
@Table(name = "user_institution_identity", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "institution_id", "user_identity_type" }) })
public class UserInstitutionIdentity implements IUniqueEntity {

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
    private PrismUserIdentity identityType;

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

    public PrismUserIdentity getIdentityType() {
        return identityType;
    }

    public void setIdentityType(PrismUserIdentity identityType) {
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

    public UserInstitutionIdentity withIdentityType(PrismUserIdentity identityType) {
        this.identityType = identityType;
        return this;
    }

    public UserInstitutionIdentity withIdentitier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("user", user).addProperty("institution", institution).addProperty("identityType", identityType);
    }

}
