package gigabank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(springSecurityFilterChain))
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void basicHello_successUser() throws Exception {
        mockMvc.perform(get("/auth/basic/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Basic Auth"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void basicHello_successAdmin() throws Exception {
        mockMvc.perform(get("/auth/basic/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Basic Auth"));
    }

    @Test
    @WithAnonymousUser
    public void basicHello_failUnauthorized() throws Exception {
        mockMvc.perform(get("/auth/basic/hello"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void basicUserHello_successUser() throws Exception {
        mockMvc.perform(get("/auth/basic/user/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Basic User Auth"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void basicUserHello_notFound() throws Exception {
        mockMvc.perform(get("/auth/basic/user/hello_test"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void basicUserHello_failAdmin() throws Exception {
        mockMvc.perform(get("/auth/basic/user/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    public void basicAdmin_failUser() throws Exception {
        mockMvc.perform(get("/auth/basic/admin/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void basicAdmin_successAdmin() throws Exception {
        mockMvc.perform(get("/auth/basic/admin/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Basic Admin Auth"));
    }
}
