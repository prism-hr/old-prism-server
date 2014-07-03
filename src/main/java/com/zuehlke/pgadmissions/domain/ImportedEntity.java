package com.zuehlke.pgadmissions.domain;

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
