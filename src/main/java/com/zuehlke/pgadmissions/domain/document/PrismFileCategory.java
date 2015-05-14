package com.zuehlke.pgadmissions.domain.document;

import com.zuehlke.pgadmissions.services.helpers.persisters.ImageDocumentPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.InstitutionBackgroundPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.InstitutionLogoPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.UserPortraitPersister;
import com.zuehlke.pgadmissions.services.helpers.processors.ImageDocumentProcessor;
import com.zuehlke.pgadmissions.services.helpers.processors.InstitutionLogoProcessor;

public enum PrismFileCategory {

    DOCUMENT, //
    IMAGE;

    public enum PrismImageCategory {

        INSTITUTION_LOGO(InstitutionLogoProcessor.class, InstitutionLogoPersister.class), //
        INSTITUTION_BACKGROUND(null, InstitutionBackgroundPersister.class), //
        USER_PORTRAIT(null, UserPortraitPersister.class);

        private Class<? extends ImageDocumentProcessor> imageProcessor;
        
        private Class<? extends ImageDocumentPersister> imagePersister;

        private PrismImageCategory(Class<? extends ImageDocumentProcessor> imageProcessor, Class<? extends ImageDocumentPersister> imagePersister) {
            this.imageProcessor = imageProcessor;
            this.imagePersister = imagePersister;
        }
        
        public Class<? extends ImageDocumentProcessor> getImageProcessor() {
            return imageProcessor;
        }

        public Class<? extends ImageDocumentPersister> getImagePersister() {
            return imagePersister;
        }

    }

}
