package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.ApplicationUpdateScope;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.domain.enums.AuthorityScope;

@Entity(name = "APPLICATION_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private Authority id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "application_role_scope_id")
    private AuthorityScope authorityScope;
    
    @Column(name = "update_visibility")
    @Enumerated(EnumType.ORDINAL)
    private ApplicationUpdateScope updateVisibility = ApplicationUpdateScope.ALL_USERS;
    
    @Column(name = "do_send_update_notification")
    private Boolean doSendUpdateNotification = false;
    
    public Authority getId() {
        return id;
    }

    public void setId(Authority id) {
        this.id = id;
    }
    
    @Override
    public String getAuthority() {
        return id.toString();
    }
    
    public AuthorityScope getAuthorityScope() {
        return authorityScope;
    }

    public void setAuthorityScope(AuthorityScope authorityScope) {
        this.authorityScope = authorityScope;
    }

    public ApplicationUpdateScope getUpdateVisibility() {
        return updateVisibility;
    }

    public void setUpdateVisibility(ApplicationUpdateScope updateVisibility) {
        this.updateVisibility = updateVisibility;
    }
    
    public Boolean getDoSendUpdateNotification() {
        return doSendUpdateNotification;
    }

    public void setDoSendUpdateNotification(Boolean doSendUpdateNotification) {
        this.doSendUpdateNotification = doSendUpdateNotification;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
        return Objects.equal(id, other.getId());
    }
    
}
