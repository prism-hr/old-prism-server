package com.zuehlke.pgadmissions.services;

import java.util.Set;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zuehlke.pgadmissions.domain.User;

@Service
public class SwitchUserService {

    public SwitchUserService() {
    }
    
    @Transactional
    public Authentication authenticate(Authentication preProcessToken) throws AuthenticationException {
        if (preProcessToken.getPrincipal() == null || preProcessToken.getCredentials() == null) {
            throw new BadCredentialsException("missing username or password");
        }
        
        UsernamePasswordAuthenticationToken authentication = null;
        
        User currentAccount = (User) preProcessToken.getPrincipal();
        User desiredAccount = (User) preProcessToken.getCredentials();
        
        User primary = currentAccount.getParentUser();
        if (primary == null) {
            primary = currentAccount;
        }
        
        if (!desiredAccount.getId().equals(primary.getId()) && !listContainsId(desiredAccount, primary.getLinkedAccounts())) {
            throw new BadCredentialsException("accounts not linked");
        }
        
        if (!currentAccount.isEnabled() || !desiredAccount.isEnabled()) {
            throw new DisabledException("account disabled");
        }
        
        if (!currentAccount.isAccountNonExpired() || !desiredAccount.isAccountNonExpired()) {
            throw new AccountExpiredException("account expired");
        }
        
        if (!currentAccount.isAccountNonLocked() || !desiredAccount.isAccountNonLocked()) {
            throw new LockedException("account locked");
        }
        
        if (!currentAccount.isCredentialsNonExpired() || !desiredAccount.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("credentials for expired");
        }
        
        UserDetails user = desiredAccount;
        authentication = new UsernamePasswordAuthenticationToken(preProcessToken.getPrincipal(), preProcessToken.getCredentials(), user.getAuthorities());
        authentication.setDetails(user);
        return authentication;
    }

    private boolean listContainsId(User user, Set<User> users) {
        for (User entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }   
}
