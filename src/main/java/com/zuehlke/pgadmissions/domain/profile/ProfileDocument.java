package com.zuehlke.pgadmissions.domain.profile;

import com.zuehlke.pgadmissions.domain.document.Document;

public interface ProfileDocument<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?>> extends ProfileSection<T> {

    String getPersonalSummary();

    void setPersonalSummary(String personalSummary);

    Document getCv();

    void setCv(Document cv);

}
