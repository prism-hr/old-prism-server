package com.zuehlke.pgadmissions.domain.builders;

import com.zuehlke.pgadmissions.domain.QualificationInstitution;

public class QualificationInstitutionBuilder {
        private Integer id;
        private String code;
        private String name;
        private Boolean enabled;
        
        
        public QualificationInstitutionBuilder id(Integer id){
            this.id = id;
            return this;
        }
        
        public QualificationInstitutionBuilder code(String code){
            this.code = code;
            return this;
        }
        
        public QualificationInstitutionBuilder name(String name){
            this.name = name;
            return this;
        }
        
        
        public QualificationInstitutionBuilder enabled(Boolean enabled){
            this.enabled = enabled;
            return this;
        }
        
        public QualificationInstitution toQualificationInstitution(){
            QualificationInstitution country = new QualificationInstitution();
            country.setId(id);
            country.setCode(code);
            country.setName(name);
            country.setEnabled(enabled);
            return country;
        }

}
