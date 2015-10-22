package com.zuehlke.pgadmissions.domain.document;

import com.zuehlke.pgadmissions.services.helpers.persisters.DepartmentBackgroundPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.ImageDocumentPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.InstitutionBackgroundPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.InstitutionLogoPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.ProgramBackgroundPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.ProjectBackgroundPersister;
import com.zuehlke.pgadmissions.services.helpers.persisters.UserPortraitPersister;
import com.zuehlke.pgadmissions.services.helpers.processors.ImageDocumentProcessor;
import com.zuehlke.pgadmissions.services.helpers.processors.InstitutionLogoProcessor;

public enum PrismFileCategory {

    DOCUMENT,
    IMAGE;

    public enum PrismImageCategory {

        // 22/10/2015
        // We need to downsample this by default for email
        // Juan and Alastair will fix it next week - better downsampling
        INSTITUTION_LOGO(InstitutionLogoProcessor.class, InstitutionLogoPersister.class), //
        INSTITUTION_BACKGROUND(null, InstitutionBackgroundPersister.class), //
        DEPARTMENT_BACKGROUND(null, DepartmentBackgroundPersister.class), //
        PROGRAM_BACKGROUND(null, ProgramBackgroundPersister.class), //
        PROJECT_BACKGROUND(null, ProjectBackgroundPersister.class), //
        USER_PORTRAIT(null, UserPortraitPersister.class);

        private Class<? extends ImageDocumentProcessor> imageProcessor;

        private Class<? extends ImageDocumentPersister> imagePersister;

        PrismImageCategory(Class<? extends ImageDocumentProcessor> imageProcessor, Class<? extends ImageDocumentPersister> imagePersister) {
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
