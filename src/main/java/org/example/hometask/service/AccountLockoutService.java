package org.example.hometask.service;

import org.example.hometask.repository.AccountRepository;
import org.example.hometask.security.entity.Account;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountLockoutService {

    private static final int MAX_FAILED_ATTEMPTS = 10;
    private final AccountRepository accountRepository;

    public AccountLockoutService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void registerFailedAttempt(String username) {
        accountRepository.findByUsername(username).ifPresent(account -> {
            if (account.isAccountNonLocked()) {
                int newAttempts = account.getFailedLoginAttempts() + 1;
                account.setFailedLoginAttempts(newAttempts);
                
                if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                    account.setAccountNonLocked(false);
                }
                accountRepository.save(account);
            }
        });
    }

    @Transactional
    public void resetFailedAttempts(String username) {
        accountRepository.findByUsername(username).ifPresent(account -> {
            if (account.getFailedLoginAttempts() > 0) {
                account.setFailedLoginAttempts(0);
                accountRepository.save(account);
            }
        });
    }

    @Transactional
    public void unlockAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("Аккаунт с ID " + accountId + " не найден"));
        
        account.setAccountNonLocked(true);
        account.setFailedLoginAttempts(0);
        accountRepository.save(account);
    }
}