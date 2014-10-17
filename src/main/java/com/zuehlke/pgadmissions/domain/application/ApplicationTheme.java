package com.zuehlke.pgadmissions.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.zuehlke.pgadmissions.domain.IUniqueEntity.ResourceSignature;

@Entity
@Table(name = "APPLICATION_THEME", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "theme" }),
        @UniqueConstraint(columnNames = { "theme", "application_id" }) })
public class ApplicationTheme {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", updatable = false, insertable = false)
    private Application application;

    @Column(name = "theme", nullable = false)
    private String theme;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public final Application getApplication() {
        return application;
    }

    public final void setApplication(Application application) {
        this.application = application;
    }

    public final String getTheme() {
        return theme;
    }

    public final void setTheme(String theme) {
        this.theme = theme;
    }

    public ResourceSignature getResourceSignature() {
        return new ResourceSignature().addProperty("application", application).addProperty("theme", theme);
    }
    
}
