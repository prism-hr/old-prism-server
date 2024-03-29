package uk.co.alumeni.prism.services.lifecycle.helpers;

public abstract class PrismServiceHelperAbstract implements PrismServiceHelper {

    @Override
    public void shutdown() {
        getShuttingDown().set(true);
    }

    @Override
    public boolean isShuttingDown() {
        return getShuttingDown().get();
    }

}
