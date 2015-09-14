package com.zuehlke.pgadmissions.domain.application;

import com.zuehlke.pgadmissions.domain.advert.Advert;

public abstract class ApplicationAdvertRelationSection extends ApplicationSection {

    public abstract Advert getAdvert();
    
    public abstract void setAdvert(Advert advert);
    
    
}
