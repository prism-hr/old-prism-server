package com.zuehlke.pgadmissions.services.integration;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.domain.user.UserAccountExternal;
import com.zuehlke.pgadmissions.rest.representation.UserRepresentation;

@Service
@Transactional
public class IntegrationUserService {

    public UserRepresentation getUserRepresentation(User user) {
        UserRepresentation representation = new UserRepresentation().withId(user.getId()).withFirstName(user.getFirstName())
                .withFirstName2(user.getFirstName2()).withFirstName3(user.getEmail());

        UserAccountExternal userAccountExternal = user.getUserAccount().getPrimaryExternalAccount();
        if (userAccountExternal != null) {
            representation.setAccountProfileUrl(userAccountExternal.getAccountProfileUrl());
            representation.setAccountImageUrl(userAccountExternal.getAccountImageUrl());
        }

        return representation;
    }

}
