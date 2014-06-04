package com.zuehlke.pgadmissions.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.enums.PrismScope;

@Entity
@Table(name = "SCOPE")
public class Scope {

    @Id
    @Column(name = "id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismScope id;
    
    @Column(name = "precedence", nullable = false, unique = true)
    private Integer precedence;

    public PrismScope getId() {
        return id;
    }

    public void setId(PrismScope id) {
        this.id = id;
    }
    
    public Integer getPrecedence() {
        return precedence;
    }

    public void setPrecedence(Integer precedence) {
        this.precedence = precedence;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Scope otherScope = (Scope) object;
        return Objects.equal(id, otherScope.getId());
    }
    
}
