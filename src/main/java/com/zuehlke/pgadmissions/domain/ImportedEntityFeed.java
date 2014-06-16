package com.zuehlke.pgadmissions.domain;

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

import com.zuehlke.pgadmissions.domain.enums.PrismImportedEntityType;

@Entity
@Table(name = "imported_entity_feed", uniqueConstraints = { @UniqueConstraint(columnNames = { "institution_id", "imported_entity_type_id" }) })
public class ImportedEntityFeed {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @Column(name = "imported_entity_type_id", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrismImportedEntityType importedEntityType;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "location", nullable = false)
    private String location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public PrismImportedEntityType getImportedEntityType() {
        return importedEntityType;
    }

    public void setImportedEntityType(PrismImportedEntityType importedEntityType) {
        this.importedEntityType = importedEntityType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
