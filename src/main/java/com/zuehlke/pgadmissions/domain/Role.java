package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.lucene.store.Lock.With;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.zuehlke.pgadmissions.domain.enums.Authority;

@Entity
@Table(name = "ROLE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Role implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 4265990408553249748L;

    @Id
    @Enumerated(EnumType.STRING)
    private Authority id;

    @ManyToMany
    @JoinTable(name = "COMMENT_ACTION_VISIBILITY", joinColumns = { @JoinColumn(name = "application_role_id") }, inverseJoinColumns = { @JoinColumn(name = "action_id") })
    private Set<Action> visibleActions = Sets.newHashSet();

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

    public Set<Action> getVisibleActions() {
        return visibleActions;
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
    
    public Role withId(Authority id) {
        this.id = id;
        return this;
    }
    
}
