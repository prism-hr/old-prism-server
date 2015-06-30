package com.zuehlke.pgadmissions.domain.department;

import com.zuehlke.pgadmissions.domain.UniqueEntity;
import com.zuehlke.pgadmissions.domain.institution.Institution;

import javax.persistence.*;

@Entity
@Table(name = "department", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "title" }) })
public class Department implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "title", nullable = false)
    private String title;

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final Institution getInstitution() {
        return institution;
    }

    public final void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public Department withInstitution(Institution institution) {
        this.institution = institution;
        return this;
    }

    public Department withTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("institution", institution).addProperty("title", title);
    }

}
