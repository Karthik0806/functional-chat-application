package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.dto.UserResponse;
import com.karthik.functionalchatapplication.security.JwtFilter;
import com.karthik.functionalchatapplication.service.OnlineUserService;
import com.karthik.functionalchatapplication.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean UserService userService;
    @MockitoBean
    OnlineUserService onlineUserService;

    @Test
    @DisplayName("GET /users/online returns set of online users")
    @WithMockUser
    void getOnlineUsers() throws Exception {
        when(onlineUserService.getOnlineUsers()).thenReturn(Set.of("alice"));

        mockMvc.perform(get("/users/online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/search returns matched users excluding current user")
    @WithMockUser(username = "karthik")
    void searchUsers() throws Exception {
        var result = List.of(
                UserResponse.builder().username("karthik_dev").online(true).build(),
                UserResponse.builder().username("kart_123").online(false).build()
        );
        when(userService.searchUsers("kart", "karthik")).thenReturn(result);

        mockMvc.perform(get("/users/search").param("keyword", "kart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("karthik_dev"))
                .andExpect(jsonPath("$[0].online").value(true))
                .andExpect(jsonPath("$[1].online").value(false));
    }

    @Test
    @DisplayName("GET /users/search returns 401 when unauthenticated")
    void searchRequiresAuth() throws Exception {
        mockMvc.perform(get("/users/search").param("keyword", "kart"))
                .andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/oauth2/authorization/google"));;
    }

    @Test
    @DisplayName("GET /users/search returns empty list when no matches")
    @WithMockUser(username = "karthik")
    void noSearchResults() throws Exception {
        when(userService.searchUsers("xyz", "karthik")).thenReturn(List.of());

        mockMvc.perform(get("/users/search").param("keyword", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
