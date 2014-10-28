package com.zuehlke.pgadmissions.rest.representation.resource.advert;

import java.util.List;
import java.util.Set;

import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertDomain;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertFunction;
import com.zuehlke.pgadmissions.domain.definitions.PrismAdvertIndustry;
import com.zuehlke.pgadmissions.domain.definitions.PrismProgramType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionAddressRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.InstitutionRepresentation;

public class AdvertRepresentation {

    private Integer id;

    private String title;

    private String summary;

    private String description;

    private String homepage;

    private String applyHomepage;

    private InstitutionAddressRepresentation address;

    private Integer studyDurationMinimum;

    private Integer studyDurationMaximum;

    private FinancialDetailsRepresentation fee;

    private FinancialDetailsRepresentation pay;

    private AdvertClosingDateRepresentation closingDate;

    private List<AdvertClosingDateRepresentation> closingDates;

    private UserRepresentation user;

    private PrismScope resourceScope;

    private Set<PrismAdvertDomain> domains;

    private Set<PrismAdvertIndustry> industries;

    private Set<PrismAdvertFunction> functions;

    private List<String> competencies;

    private List<String> themes;

    private List<Integer> institutions;

    private List<PrismProgramType> programTypes;

    private PrismProgramType programType;

    private Set<PrismStudyOption> studyOptions;

    private InstitutionRepresentation institution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final String getHomepage() {
        return homepage;
    }

    public final void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public final String getApplyHomepage() {
        return applyHomepage;
    }

    public final void setApplyHomepage(String applyHomepage) {
        this.applyHomepage = applyHomepage;
    }

    public InstitutionAddressRepresentation getAddress() {
        return address;
    }

    public void setAddress(InstitutionAddressRepresentation address) {
        this.address = address;
    }

    public Integer getStudyDurationMinimum() {
        return studyDurationMinimum;
    }

    public void setStudyDurationMinimum(Integer studyDurationMinimum) {
        this.studyDurationMinimum = studyDurationMinimum;
    }

    public Integer getStudyDurationMaximum() {
        return studyDurationMaximum;
    }

    public void setStudyDurationMaximum(Integer studyDurationMaximum) {
        this.studyDurationMaximum = studyDurationMaximum;
    }

    public FinancialDetailsRepresentation getFee() {
        return fee;
    }

    public void setFee(FinancialDetailsRepresentation fee) {
        this.fee = fee;
    }

    public FinancialDetailsRepresentation getPay() {
        return pay;
    }

    public void setPay(FinancialDetailsRepresentation pay) {
        this.pay = pay;
    }

    public AdvertClosingDateRepresentation getClosingDate() {
        return closingDate;
    }

    public void setClosingDate(AdvertClosingDateRepresentation closingDate) {
        this.closingDate = closingDate;
    }

    public List<AdvertClosingDateRepresentation> getClosingDates() {
        return closingDates;
    }

    public void setClosingDates(List<AdvertClosingDateRepresentation> closingDates) {
        this.closingDates = closingDates;
    }

    public UserRepresentation getUser() {
        return user;
    }

    public void setUser(UserRepresentation user) {
        this.user = user;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

    public Set<PrismAdvertDomain> getDomains() {
        return domains;
    }

    public void setDomains(Set<PrismAdvertDomain> domains) {
        this.domains = domains;
    }

    public Set<PrismAdvertIndustry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<PrismAdvertIndustry> industries) {
        this.industries = industries;
    }

    public Set<PrismAdvertFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<PrismAdvertFunction> functions) {
        this.functions = functions;
    }

    public List<String> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(List<String> competencies) {
        this.competencies = competencies;
    }

    public List<String> getThemes() {
        return themes;
    }

    public void setThemes(List<String> themes) {
        this.themes = themes;
    }

    public final List<Integer> getInstitutions() {
        return institutions;
    }

    public final void setInstitutions(List<Integer> institutions) {
        this.institutions = institutions;
    }

    public final List<PrismProgramType> getProgramTypes() {
        return programTypes;
    }

    public final void setProgramTypes(List<PrismProgramType> programTypes) {
        this.programTypes = programTypes;
    }

    public final PrismProgramType getProgramType() {
        return programType;
    }

    public final void setProgramType(PrismProgramType programType) {
        this.programType = programType;
    }

    public Set<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(Set<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

    public InstitutionRepresentation getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionRepresentation institution) {
        this.institution = institution;
    }

}
