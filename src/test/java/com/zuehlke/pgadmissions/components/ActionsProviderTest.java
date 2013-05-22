package com.zuehlke.pgadmissions.components;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Preconditions;
import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.ActionsDefinitions;
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

        assertActionsDefinition(actions, false, new String[] { "emailApplicant", "view" }, new String[] { "Email applicant", "View" });
    }

    private void assertActionsDefinition(ActionsDefinitions actionsDefinition, boolean requiresAttention, String[] actionsNames, String[] actionDisplayValues) {
        Preconditions.checkArgument(actionsNames.length == actionDisplayValues.length);

        Map<String, String> actions = actionsDefinition.getActions();
        assertEquals("Expected actions: " + Arrays.toString(actionsNames) + ", got: " + actions.keySet(), actionsNames.length, actions.size());
        for (int i = 0; i < actionsNames.length; i++) {
            assertEquals("Got actions: " + actions.keySet(), actionDisplayValues[i], actions.get(actionsNames[i]));
        }

        assertEquals(requiresAttention, actionsDefinition.isRequiresAttention());
    }

}
