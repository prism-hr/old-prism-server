package uk.co.alumeni.prism.dto;

import com.google.common.base.Objects;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismAction;
import uk.co.alumeni.prism.domain.definitions.workflow.PrismActionEnhancement;

public class ActionEnhancementDTO {

    private PrismAction action;

    private PrismActionEnhancement actionEnhancement;

    public PrismAction getAction() {
        return action;
    }

    public void setAction(PrismAction action) {
        this.action = action;
    }

    public PrismActionEnhancement getActionEnhancement() {
        return actionEnhancement;
    }

    public void setActionEnhancement(PrismActionEnhancement actionEnhancement) {
        this.actionEnhancement = actionEnhancement;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(action, actionEnhancement);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final ActionEnhancementDTO other = (ActionEnhancementDTO) object;
        return Objects.equal(action, other.getAction()) && Objects.equal(actionEnhancement, other.getActionEnhancement());
    }

}
