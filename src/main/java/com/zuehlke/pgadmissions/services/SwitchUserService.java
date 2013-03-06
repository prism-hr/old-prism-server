package com.zuehlke.pgadmissions.services;

import java.util.List;

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

import com.zuehlke.pgadmissions.domain.RegisteredUser;

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
        
        RegisteredUser currentAccount = (RegisteredUser) preProcessToken.getPrincipal();
        RegisteredUser desiredAccount = (RegisteredUser) preProcessToken.getCredentials();
        
        RegisteredUser primary = currentAccount.getPrimaryAccount();
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

    private boolean listContainsId(RegisteredUser user, List<RegisteredUser> users) {
        for (RegisteredUser entry : users) {
            if (entry.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }   
}
