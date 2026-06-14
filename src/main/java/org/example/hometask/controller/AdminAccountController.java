package org.example.hometask.controller;

import org.example.hometask.service.AccountLockoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

    private final AccountLockoutService lockoutService;

    public AdminAccountController(AccountLockoutService lockoutService) {
        this.lockoutService = lockoutService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/{id}/unlock")
    public ResponseEntity<Map<String, String>> unlockAccount(@PathVariable Long id) {
        lockoutService.unlockAccount(id);
        return ResponseEntity.ok(Map.of("message", "Аккаунт успешно разблокирован."));
    }
}