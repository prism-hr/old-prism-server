package uk.co.alumeni.prism.domain.definitions;

import uk.co.alumeni.prism.dto.AdvertCategoryNameEnumDTO;
import uk.co.alumeni.prism.dto.AdvertCategoryNameStringSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertCategorySummaryDTO;
import uk.co.alumeni.prism.dto.AdvertFunctionSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertIndustrySummaryDTO;
import uk.co.alumeni.prism.dto.AdvertInstitutionSummaryDTO;
import uk.co.alumeni.prism.dto.AdvertLocationSummaryDTO;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCategoryNameStringSummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertCategorySummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertFunctionSummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertIndustrySummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertInstitutionSummaryRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertLocationSummaryRepresentation;

public enum PrismAdvertFilterCategory {

    INSTITUTION(Integer.class, AdvertInstitutionSummaryDTO.class, AdvertInstitutionSummaryRepresentation.class),
    INDUSTRY(PrismAdvertIndustry.class, AdvertIndustrySummaryDTO.class, AdvertIndustrySummaryRepresentation.class, "industry", "industries"),
    FUNCTION(PrismAdvertFunction.class, AdvertFunctionSummaryDTO.class, AdvertFunctionSummaryRepresentation.class, "function", "functions"),
    THEME(Integer.class, AdvertCategoryNameStringSummaryDTO.class, AdvertCategoryNameStringSummaryRepresentation.class),
    LOCATION(Integer.class, AdvertLocationSummaryDTO.class, AdvertLocationSummaryRepresentation.class);

    private PrismAdvertFilterCategory(Class<?> idClass, Class<? extends AdvertCategorySummaryDTO<?>> dtoClass,
            Class<? extends AdvertCategorySummaryRepresentation<?>> representationClass) {
        this.idClass = idClass;
        this.dtoClass = dtoClass;
        this.representationClass = representationClass;
    }

    private PrismAdvertFilterCategory(Class<?> idClass, Class<? extends AdvertCategorySummaryDTO<?>> dtoClass,
            Class<? extends AdvertCategorySummaryRepresentation<?>> representationClass, String propertyName, String propertyCollectionName) {
        this(idClass, dtoClass, representationClass);
        this.propertyName = propertyName;
        this.propertyCollectionName = propertyCollectionName;
    }

    private Class<?> idClass;

    private Class<? extends AdvertCategorySummaryDTO<?>> dtoClass;

    private Class<? extends AdvertCategorySummaryRepresentation<?>> representationClass;

    private String propertyName;

    private String propertyCollectionName;

    public Class<?> getIdClass() {
        return idClass;
    }

    public Class<? extends AdvertCategorySummaryDTO<?>> getDtoClass() {
        return dtoClass;
    }

    public Class<? extends AdvertCategorySummaryRepresentation<?>> getRepresentationClass() {
        return representationClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyCollectionName() {
        return propertyCollectionName;
    }

    public boolean isNameEnumCategory() {
        return AdvertCategoryNameEnumDTO.class.isAssignableFrom(dtoClass);
    }

}
