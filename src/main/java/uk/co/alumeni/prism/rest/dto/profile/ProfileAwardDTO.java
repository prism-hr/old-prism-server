package uk.co.alumeni.prism.rest.dto.profile;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.utils.validation.DateNotFuture;

public class ProfileAwardDTO {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @DateNotFuture
    private LocalDate awardDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

}
