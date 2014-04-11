package com.zuehlke.pgadmissions.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "REJECT_REASON")
@Immutable
public class RejectReason implements Serializable {
    private static final long serialVersionUID = 2745896114174369017L;

    private String text;

    @Id
    @GeneratedValue
    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "RejectReason [" + (id != null ? "id=" + id + ", " : "id=<null>") + "text=" + text + "]";
    }

}
