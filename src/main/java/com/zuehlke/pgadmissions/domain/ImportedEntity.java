package com.zuehlke.pgadmissions.domain;

import com.google.common.base.Objects;
import org.apache.solr.analysis.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Parameter;

import javax.persistence.*;

public interface ImportedEntity {

    public Institution getInstitution() ;

    public void setInstitution(Institution institution) ;

    public String getCode() ;

    public void setCode(String code) ;

    public String getName() ;

    public void setName(String name) ;

    public boolean isEnabled() ;

    public void setEnabled(boolean enabled) ;

}
