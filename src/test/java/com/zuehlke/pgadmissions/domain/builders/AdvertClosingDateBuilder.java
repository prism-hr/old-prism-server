package com.zuehlke.pgadmissions.domain.builders;


import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.domain.AdvertClosingDate;

public class AdvertClosingDateBuilder {

    private Integer id;
    private Advert advert;
    private Integer studyPlaces;
    private LocalDate closingDate;
    
    public AdvertClosingDateBuilder id(Integer id){
        this.id = id;
        return this;
    }
    
    public AdvertClosingDateBuilder advert(Advert advert){
        this.advert = advert;
        return this;
    }
    
    public AdvertClosingDateBuilder studyPlaces(Integer studyPlaces){
        this.studyPlaces = studyPlaces;
        return this;
    }
    
    
    public AdvertClosingDateBuilder closingDate(LocalDate closingDate){
        this.closingDate = closingDate;
        return this;
    }
    
    public AdvertClosingDate build(){
    	AdvertClosingDate closingDate = new AdvertClosingDate();
        closingDate.setId(id);
        closingDate.setAdvert(advert);
        closingDate.setStudyPlaces(studyPlaces);
        closingDate.setClosingDate(this.closingDate);
        return closingDate;
    }
}
