package org.example.hometask.security;

import org.example.hometask.service.AccountLockoutService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventsListener {

    private final AccountLockoutService lockoutService;

    public AuthenticationEventsListener(AccountLockoutService lockoutService) {
        this.lockoutService = lockoutService;
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        lockoutService.registerFailedAttempt(username);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        lockoutService.resetFailedAttempts(username);
    }
}