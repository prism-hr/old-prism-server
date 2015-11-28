package uk.co.alumeni.prism.domain.application;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import uk.co.alumeni.prism.domain.Theme;
import uk.co.alumeni.prism.domain.UniqueEntity;

@Entity
@Table(name = "application_theme", uniqueConstraints = { @UniqueConstraint(columnNames = { "application_id", "theme_id" }) })
public class ApplicationTheme implements UniqueEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(name = "preference", nullable = false)
    private Boolean preference;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public Boolean getPreference() {
        return preference;
    }

    public void setPreference(Boolean preference) {
        this.preference = preference;
    }

    public ApplicationTheme withApplication(Application application) {
        this.application = application;
        return this;
    }

    public ApplicationTheme withTheme(Theme theme) {
        this.theme = theme;
        return this;
    }

    public ApplicationTheme withPreference(Boolean preference) {
        this.preference = preference;
        return this;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return new EntitySignature().addProperty("application", application).addProperty("theme", theme);
    }

}
