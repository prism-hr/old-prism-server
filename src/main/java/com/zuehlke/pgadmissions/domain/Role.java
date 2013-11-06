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
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity(name = "APPLICATION_ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private Authority id;
    
    @Column(name = "update_visibility")
    private int updateVisibility = 1;

    @Override
    public String getAuthority() {
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    public void setId(Authority id) {
        this.id = id;
    }

    public Authority getId() {
        return id;
    }
    
    public int getUpdateVisibility() {
    	return updateVisibility;
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
