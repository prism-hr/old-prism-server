package uk.co.alumeni.prism.domain.application;

public abstract class ApplicationTagSection<T> extends ApplicationSection {

    public abstract T getTag();

    public abstract void setTag(T tag);

    @Override
    public EntitySignature getEntitySignature() {
        return super.getEntitySignature().addProperty("tag", getTag());
    }

}
