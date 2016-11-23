package uk.co.alumeni.prism.domain.advert;

import uk.co.alumeni.prism.domain.Competence;
import uk.co.alumeni.prism.domain.definitions.PrismCompetenceMode;

import javax.persistence.*;

@Entity
@Table(name = "advert_competence", uniqueConstraints = {@UniqueConstraint(columnNames = {"advert_id", "competence_id"})})
public class AdvertCompetence extends AdvertAttribute {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "advert_id", nullable = false)
    private Advert advert;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    @Column(name = "description")
    private String description;

    @Column(name = "importance", nullable = false)
    private Integer importance;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode")
    private PrismCompetenceMode mode;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Advert getAdvert() {
        return advert;
    }

    @Override
    public void setAdvert(Advert advert) {
        this.advert = advert;
    }

    public Competence getCompetence() {
        return competence;
    }

    public void setCompetence(Competence competence) {
        this.competence = competence;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public PrismCompetenceMode getMode() {
        return mode;
    }

    public void setMode(PrismCompetenceMode mode) {
        this.mode = mode;
    }

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("competence", competence);
    }

}
