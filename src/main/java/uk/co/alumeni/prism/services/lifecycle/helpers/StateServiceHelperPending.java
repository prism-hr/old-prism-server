package uk.co.alumeni.prism.services.lifecycle.helpers;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.services.StateService;

@Component
public class StateServiceHelperPending extends PrismServiceHelperAbstract {

    @Inject
    private StateService stateService;

    private AtomicBoolean shuttingDown = new AtomicBoolean(false);

    @Override
    public void execute() {
        stateService.getStateActionPendings().forEach(stateActionPending -> {
            executeStateActionPending(stateActionPending);
        });
    }

    @Override
    public AtomicBoolean getShuttingDown() {
        return shuttingDown;
    }

    private void executeStateActionPending(Integer stateActionPending) {
        if (!isShuttingDown()) {
            stateService.executeStateActionPending(stateActionPending);
        }
    }

}
