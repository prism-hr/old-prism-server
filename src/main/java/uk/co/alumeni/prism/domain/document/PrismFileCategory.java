package uk.co.alumeni.prism.domain.document;

import uk.co.alumeni.prism.services.helpers.persisters.DepartmentBackgroundPersister;
import uk.co.alumeni.prism.services.helpers.persisters.ImageDocumentPersister;
import uk.co.alumeni.prism.services.helpers.persisters.InstitutionBackgroundPersister;
import uk.co.alumeni.prism.services.helpers.persisters.InstitutionLogoPersister;
import uk.co.alumeni.prism.services.helpers.persisters.ProgramBackgroundPersister;
import uk.co.alumeni.prism.services.helpers.persisters.ProjectBackgroundPersister;
import uk.co.alumeni.prism.services.helpers.persisters.UserPortraitPersister;

public enum PrismFileCategory {

    DOCUMENT,
    IMAGE;

    public enum PrismImageCategory {

        INSTITUTION_LOGO(InstitutionLogoPersister.class), //
        INSTITUTION_BACKGROUND(InstitutionBackgroundPersister.class), //
        DEPARTMENT_BACKGROUND(DepartmentBackgroundPersister.class), //
        PROGRAM_BACKGROUND(ProgramBackgroundPersister.class), //
        PROJECT_BACKGROUND(ProjectBackgroundPersister.class), //
        USER_PORTRAIT(UserPortraitPersister.class);

        private Class<? extends ImageDocumentPersister> imagePersister;

        PrismImageCategory(Class<? extends ImageDocumentPersister> imagePersister) {
            this.imagePersister = imagePersister;
        }

        public Class<? extends ImageDocumentPersister> getImagePersister() {
            return imagePersister;
        }

    }

}
