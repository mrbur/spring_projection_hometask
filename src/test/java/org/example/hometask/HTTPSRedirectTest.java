package org.example.hometask;

import org.example.hometask.security.CustomUserDetailsService;
import org.example.hometask.security.JwtAuthenticationFilter;
import org.example.hometask.security.LoggingFilter;
import org.example.hometask.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "server.forward-headers-strategy=native",
        "server.port=80",
        "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
///
/// этот тест вынесен отдельно так как для него понадобился полноценный спринг-контекст,
/// остальные тесты будут сделаны через более изолированный WebMvcTest

class HTTPSRedirectTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoSpyBean private LoggingFilter loggingFilter;
    @MockitoBean private CustomUserDetailsService customUserDetailsService;
    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void shouldRedirectToHttpsWhenRequestIsHttp() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                        .with(request -> {
                            request.setScheme("http");
                            request.setServerName("localhost");
                            request.setServerPort(80);
                            request.setSecure(false);
                            return request;
                        })
                        .header("Host", "localhost:80")
                        .header("X-Forwarded-Port", "443"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "https://localhost/api/auth/login"));
    }

}