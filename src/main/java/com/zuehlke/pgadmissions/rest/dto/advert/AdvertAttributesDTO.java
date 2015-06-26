package com.zuehlke.pgadmissions.rest.dto.advert;

import java.util.List;

public abstract class AdvertAttributesDTO <T> {

    public abstract List<T> getAttributes();
    
}
