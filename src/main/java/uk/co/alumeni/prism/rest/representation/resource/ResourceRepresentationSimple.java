package uk.co.alumeni.prism.rest.representation.resource;

import uk.co.alumeni.prism.domain.definitions.workflow.PrismScope;
import uk.co.alumeni.prism.rest.representation.DocumentRepresentation;
import uk.co.alumeni.prism.rest.representation.advert.AdvertRepresentationSimple;
import uk.co.alumeni.prism.rest.representation.state.StateRepresentationSimple;

public class ResourceRepresentationSimple extends ResourceRepresentationIdentity {

    private String code;

    private String importedCode;

    private AdvertRepresentationSimple advert;

    private StateRepresentationSimple state;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImportedCode() {
        return importedCode;
    }

    public void setImportedCode(String importedCode) {
        this.importedCode = importedCode;
    }

    public AdvertRepresentationSimple getAdvert() {
        return advert;
    }

    public void setAdvert(AdvertRepresentationSimple advert) {
        this.advert = advert;
    }

    public StateRepresentationSimple getState() {
        return state;
    }

    public void setState(StateRepresentationSimple state) {
        this.state = state;
    }

    public ResourceRepresentationSimple withScope(PrismScope scope) {
        setScope(scope);
        return this;
    }

    public ResourceRepresentationSimple withId(Integer id) {
        setId(id);
        return this;
    }

    public ResourceRepresentationSimple withName(String name) {
        setName(name);
        return this;
    }

    public ResourceRepresentationSimple withLogoImage(DocumentRepresentation logoImage) {
        setLogoImage(logoImage);
        return this;
    }

}
