package com.zuehlke.pgadmissions.components;

import static com.zuehlke.pgadmissions.dto.ApplicationFormAction.*;
import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
import com.zuehlke.pgadmissions.dto.ApplicationFormAction;
import com.zuehlke.pgadmissions.services.StateTransitionViewResolver;

public class ActionsProviderTest {

    private ActionsProvider actionsProvider;

    private StateTransitionViewResolver stateTransitionViewResolverMock;

    private RegisteredUser user;
    private ApplicationForm application;
    private ActionsAvailabilityProvider availabilityProviderMock;

    @Before
    public void setup() {
        user = new RegisteredUser();
        application = new ApplicationForm();

        availabilityProviderMock = EasyMock.createNiceMock(ActionsAvailabilityProvider.class);
        stateTransitionViewResolverMock = createMock(StateTransitionViewResolver.class);
        actionsProvider = new ActionsProvider(stateTransitionViewResolverMock, availabilityProviderMock);

    }

    @Test
    public void shouldReturnBasicActions() {
        EasyMock.expect(stateTransitionViewResolverMock.getNextStatus(application)).andReturn(null);

        EasyMock.replay(stateTransitionViewResolverMock, availabilityProviderMock);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, application);
        EasyMock.verify(stateTransitionViewResolverMock, availabilityProviderMock);

        assertActionsDefinition(actions, false, VIEW);
    }

    @Test
    public void shouldReturnViewEditActionIfCanEdit() {
        EasyMock.expect(stateTransitionViewResolverMock.getNextStatus(application)).andReturn(null);
        EasyMock.expect(availabilityProviderMock.canEdit(user, application)).andReturn(true);

        EasyMock.replay(stateTransitionViewResolverMock, availabilityProviderMock);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, application);
        EasyMock.verify(stateTransitionViewResolverMock, availabilityProviderMock);

        assertActionsDefinition(actions, false, VIEW_EDIT);
    }

    @Test
    public void shouldReturnValidateAction() {
        EasyMock.expect(stateTransitionViewResolverMock.getNextStatus(application)).andReturn(null);
        EasyMock.expect(availabilityProviderMock.canCompleteValidationStage(user, application, null)).andReturn(true);

        EasyMock.replay(stateTransitionViewResolverMock, availabilityProviderMock);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, application);
        EasyMock.verify(stateTransitionViewResolverMock, availabilityProviderMock);

        assertActionsDefinition(actions, false, VIEW, COMPLETE_VALIDATION_STAGE);
    }

    @Test
    public void shouldReturnEvaluateReviewsAction() {
        EasyMock.expect(stateTransitionViewResolverMock.getNextStatus(application)).andReturn(null);
        EasyMock.expect(availabilityProviderMock.canCompleteReviewStage(user, application, null)).andReturn(true);

        EasyMock.replay(stateTransitionViewResolverMock, availabilityProviderMock);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, application);
        EasyMock.verify(stateTransitionViewResolverMock, availabilityProviderMock);

        assertActionsDefinition(actions, false, VIEW, COMPLETE_REVIEW_STAGE);
    }
    
    @Test
    public void shouldConfirmEligibilityAction() {
        EasyMock.expect(stateTransitionViewResolverMock.getNextStatus(application)).andReturn(null);
        EasyMock.expect(availabilityProviderMock.canConfirmEligibility(user, application)).andReturn(true);
        EasyMock.expect(availabilityProviderMock.isEligibilityConfirmationAwaiting(application)).andReturn(true);

        EasyMock.replay(stateTransitionViewResolverMock, availabilityProviderMock);
        ActionsDefinitions actions = actionsProvider.calculateActions(user, application);
        EasyMock.verify(stateTransitionViewResolverMock, availabilityProviderMock);

        assertActionsDefinition(actions, true, VIEW, CONFIRM_ELIGIBILITY);
    }
    
    private void assertActionsDefinition(ActionsDefinitions actionsDefinition, boolean requiresAttention, ApplicationFormAction... actions) {
        Assert.assertThat(actionsDefinition.getActions(), CoreMatchers.hasItems(actions));
        assertEquals(requiresAttention, actionsDefinition.isRequiresAttention());
    }

}
