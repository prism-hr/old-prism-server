package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.document.Document;

public interface ProfileDocument<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    String getPersonalSummary();

    void setPersonalSummary(String personalSummary);

    Document getCv();

    void setCv(Document cv);

}
