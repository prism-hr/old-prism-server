package com.zuehlke.pgadmissions.dto.resource;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.copyProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.getProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.hasProperty;
import static com.zuehlke.pgadmissions.utils.PrismReflectionUtils.setProperty;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceActivityDTO implements Comparable<Object> {

    private static final String ID = "Id";

    private static final String NAME = "Name";

    private static final String LOGO_IMAGE_ID = "LogoImageId";

    private Integer systemId;

    private Integer institutionId;

    private String institutionName;

    private Integer institutionLogoImageId;

    private Integer departmentId;

    private String departmentName;

    private Integer programId;

    private String programName;

    private Integer projectId;

    private String projectName;

    private Integer applicationId;

    public Integer getSystemId() {
        return systemId;
    }

    public void setSystemId(Integer systemId) {
        this.systemId = systemId;
    }

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Integer getInstitutionLogoImageId() {
        return institutionLogoImageId;
    }

    public void setInstitutionLogoImageId(Integer institutionLogoImageId) {
        this.institutionLogoImageId = institutionLogoImageId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public PrismScope getScope() {
        PrismScope[] scopes = PrismScope.values();
        for (int i = (scopes.length - 1); i >= 0; i--) {
            PrismScope scope = scopes[i];
            if (getProperty(this, scope.getLowerCamelName() + ID) != null) {
                return scope;
            }
        }
        return null;
    }

    public Integer getId() {
        PrismScope[] scopes = PrismScope.values();
        for (int i = (scopes.length - 1); i >= 0; i--) {
            Integer id = getId(scopes[i]);
            if (id != null) {
                return id;
            }
        }
        return null;
    }

    public String getName() {
        PrismScope[] scopes = PrismScope.values();
        for (int i = (scopes.length - 1); i >= 0; i--) {
            String name = getName(scopes[i]);
            if (name != null) {
                return name;
            }
        }
        return null;
    }

    public Integer getLogoImage() {
        return getScope().equals(INSTITUTION) ? institutionLogoImageId : null;
    }

    public ResourceActivityDTO getEnclosingResource(PrismScope scope) {
        return getEnclosingResource(scope, ResourceActivityDTO.class);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getScope());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ResourceActivityDTO other = (ResourceActivityDTO) object;
        return Objects.equal(getScope(), other.getScope()) && Objects.equal(getId(), other.getId());
    }

    @Override
    public int compareTo(Object object) {
        ResourceActivityDTO other = (ResourceActivityDTO) object;
        Integer scopeOrdinal = getScopeOrdinal();
        Integer otherScopeOrdinal = other.getScopeOrdinal();

        int scopeComparison = scopeOrdinal.compareTo(otherScopeOrdinal);
        if (scopeComparison == 0) {
            if (!(scopeOrdinal == 0 || otherScopeOrdinal == 0)) {
                int nameComparison = getName().compareTo(other.getName());
                return (nameComparison == 0) ? getId().compareTo(other.getId()) : nameComparison;
            }

            return getId().compareTo(other.getId());
        }
        return scopeComparison;
    }

    protected <T extends ResourceActivityDTO> T getEnclosingResource(PrismScope scope, Class<T> returnType) {
        T enclosingResource = BeanUtils.instantiate(returnType);

        boolean cloning = false;
        PrismScope[] parentScopes = PrismScope.values();
        for (int i = (parentScopes.length - 1); i >= 0; i--) {
            PrismScope parentScope = parentScopes[i];
            cloning = scope.equals(parentScope) ? true : cloning;

            if (cloning) {
                String scopeReference = scope.getLowerCamelName();
                copyProperty(this, enclosingResource, scopeReference + ID);
                copyProperty(this, enclosingResource, scopeReference + NAME);
                copyProperty(this, enclosingResource, scopeReference + LOGO_IMAGE_ID);
            }
        }

        return enclosingResource.getId() == null ? null : enclosingResource;
    }

    protected Integer getScopeOrdinal() {
        PrismScope scope = getScope();
        return scope == null ? null : scope.ordinal();
    }

    protected void setId(PrismScope scope, Integer id) {
        setProperty(this, scope.getLowerCamelName() + ID, id);
    }

    private Integer getId(PrismScope scope) {
        Integer id = (Integer) getProperty(this, scope.getLowerCamelName() + ID);
        if (id != null) {
            return id;
        }
        return null;
    }

    private String getName(PrismScope scope) {
        String scopeReference = scope.getLowerCamelName();
        if (getProperty(this, scopeReference + ID) != null) {
            if (hasProperty(this, scopeReference + NAME)) {
                return (String) getProperty(this, scopeReference + NAME);
            }
        }
        return null;
    }

}
