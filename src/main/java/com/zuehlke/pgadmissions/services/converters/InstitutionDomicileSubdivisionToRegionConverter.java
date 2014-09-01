package com.zuehlke.pgadmissions.services.converters;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.InstitutionDomicile;
import com.zuehlke.pgadmissions.domain.InstitutionDomicileRegion;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionLocaleType;
import com.zuehlke.pgadmissions.iso.jaxb.SubdivisionType;

public class InstitutionDomicileSubdivisionToRegionConverter implements Function<SubdivisionType, Iterable<InstitutionDomicileRegion>> {

    private InstitutionDomicile domicile;

    private InstitutionDomicileRegion parentRegion;

    private Map<Short, String> categories;

    public InstitutionDomicileSubdivisionToRegionConverter(InstitutionDomicile domicile, InstitutionDomicileRegion parentRegion, Map<Short, String> categories) {
        this.domicile = domicile;
        this.parentRegion = parentRegion;
        this.categories = categories;
    }

    @Override
    public Iterable<InstitutionDomicileRegion> apply(SubdivisionType subdivision) {
        List<SubdivisionLocaleType> locales = subdivision.getSubdivisionLocale();
        String name = locales.get(0).getSubdivisionLocaleName();
        List<String> otherNames = Lists.newLinkedList();
        for (int i = 1; i < locales.size(); i++) {
            otherNames.add(locales.get(i).getSubdivisionLocaleName());
        }
        String otherName = null;
        if (!otherNames.isEmpty()) {
            otherName = Joiner.on(",").join(otherNames);
        }
        String regionType = categories.get(subdivision.getCategoryId());

        InstitutionDomicileRegion currentRegion = new InstitutionDomicileRegion().withId(subdivision.getSubdivisionCode().getValue()).withEnabled(true)
                .withDomicile(domicile).withParentRegion(parentRegion).withName(name).withOtherName(otherName).withRegionType(regionType);

        if (subdivision.getSubdivision().isEmpty()) {
            return Lists.newArrayList(currentRegion);
        }
        InstitutionDomicileSubdivisionToRegionConverter subConverter = new InstitutionDomicileSubdivisionToRegionConverter(domicile, currentRegion, categories);
        Iterable<InstitutionDomicileRegion> subregions = Iterables.concat(Iterables.transform(subdivision.getSubdivision(), subConverter));
        return Iterables.concat(Collections.singleton(currentRegion), subregions);
    }

}
