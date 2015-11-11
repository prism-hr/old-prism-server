package com.zuehlke.pgadmissions.domain;

import org.joda.time.DateTime;

public interface Activity {

    public Integer getId();

    public void setId(Integer id);

    public String getContent();

    public void setContent(String content);

    public DateTime getCreatedTimestamp();

    public void setCreatedTimestamp(DateTime createdTimestamp);

    public String getSequenceIdentifier();

    public void setSequenceIdentifier(String sequenceIdentifier);

}
