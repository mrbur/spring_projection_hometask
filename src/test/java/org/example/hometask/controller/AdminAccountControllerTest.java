package org.example.hometask.controller;

import org.example.hometask.service.AccountLockoutService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.liquibase.enabled=false",
        "server.forward-headers-strategy=native"
})
@AutoConfigureMockMvc
class AdminAccountControllerHardcoreTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountLockoutService lockoutService;

    @Test
    void unlockAccount_ShouldReturnOk_WhenUserIsSuperAdmin() throws Exception {
        Long accountId = 1L;

        mockMvc.perform(post("/api/admin/accounts/{id}/unlock", accountId)
                        .header("X-Forwarded-Proto", "https")
                        .header("X-Forwarded-Port", "443")
                        .with(user("admin").roles("SUPER_ADMIN"))
                        .with(csrf())
                        .with(request -> {
                            request.setScheme("https");
                            request.setServerPort(443);
                            request.setSecure(true);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Аккаунт успешно разблокирован."));

        verify(lockoutService).unlockAccount(accountId);
    }

    @Test
    void unlockAccount_ShouldReturnForbidden_WhenUserIsNotSuperAdmin() throws Exception {
        Long accountId = 1L;

        mockMvc.perform(post("/api/admin/accounts/{id}/unlock", accountId)
                        .secure(true)
                        .header("X-Forwarded-Proto", "https")
                        .header("X-Forwarded-Port", "443")
                        .with(request -> {
                            request.setScheme("https");
                            request.setServerPort(443);
                            request.setSecure(true);
                            return request;
                        })
                        .with(user("moderator").roles("MODERATOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unlockAccount_ShouldReturnForbidden_WhenUserIsRegularAdmin() throws Exception {
        Long accountId = 2L;

        mockMvc.perform(post("/api/admin/accounts/{id}/unlock", accountId)
                        .with(request -> {
                            request.setScheme("https");
                            request.setServerPort(443);
                            request.setSecure(true);
                            return request;
                        })
                        .secure(true)
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(lockoutService, Mockito.never()).unlockAccount(accountId);
    }

    @Test
    void unlockAccount_ShouldReturnForbidden_WhenUserIsAnonymous() throws Exception {
        Long accountId = 3L;

        mockMvc.perform(post("/api/admin/accounts/{id}/unlock", accountId)
                        .with(request -> {
                            request.setScheme("https");
                            request.setServerPort(443);
                            request.setSecure(true);
                            return request;
                        })
                        .header("X-Forwarded-Proto", "https")
                        .header("X-Forwarded-Port", "443")
                        .with(csrf())
                        .with(anonymous()))
                .andExpect(status().isForbidden());
    }
}