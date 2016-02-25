package uk.co.alumeni.prism.rest.dto.profile;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import uk.co.alumeni.prism.rest.dto.DocumentDTO;

public class ProfileDocumentDTO {

    @NotEmpty
    @Size(max = 5000)
    private String personalSummary;

    @Valid
    private DocumentDTO cv;

    @Valid
    private DocumentDTO coveringLetter;

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    public DocumentDTO getCv() {
        return cv;
    }

    public void setCv(DocumentDTO cv) {
        this.cv = cv;
    }

    public DocumentDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(DocumentDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

}
