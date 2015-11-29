package uk.co.alumeni.prism.domain.profile;

import uk.co.alumeni.prism.domain.document.Document;

public interface ProfileQualification<T extends ProfileEntity<?, ?, ?, ?, ?, ?, ?, ?>> extends ProfileAdvertRelationSection<T> {

    Integer getStartYear();

    void setStartYear(Integer startYear);

    Integer getStartMonth();

    void setStartMonth(Integer startMonth);

    Integer getAwardYear();

    void setAwardYear(Integer awardYear);

    Integer getAwardMonth();

    void setAwardMonth(Integer awardMonth);

    String getGrade();

    void setGrade(String grade);

    Boolean getCompleted();

    void setCompleted(Boolean completed);

    Document getDocument();

    void setDocument(Document document);

}
