package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.Enumeration.AuthProvider;
import com.karthik.functionalchatapplication.dto.UserResponse;
import com.karthik.functionalchatapplication.entity.User;
import com.karthik.functionalchatapplication.repo.UserRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepo userRepo;
    @Mock OnlineUserService onlineUserService;
    @InjectMocks UserService userService;

    @Test
    @DisplayName("returns users matching keyword, excluding the current user")
    void excludesCurrentUser() {
        when(userRepo.findTop10ByUsernameContainingIgnoreCase("art"))
                .thenReturn(List.of(user("karthik"), user("art_guy"), user("alice")));
        when(onlineUserService.isOnline(any())).thenReturn(false);

        List<UserResponse> result = userService.searchUsers("art", "karthik");

        assertThat(result).extracting(UserResponse::getUsername)
                .containsExactlyInAnyOrder("art_guy", "alice")
                .doesNotContain("karthik");
    }

    @Test
    @DisplayName("marks user as online when OnlineUserService says so")
    void marksOnlineStatus() {
        when(userRepo.findTop10ByUsernameContainingIgnoreCase("bob"))
                .thenReturn(List.of(user("bob"), user("bobby")));
        when(onlineUserService.isOnline("bob")).thenReturn(true);
        when(onlineUserService.isOnline("bobby")).thenReturn(false);

        List<UserResponse> result = userService.searchUsers("bob", "nobody");

        assertThat(result).filteredOn(UserResponse::isOnline)
                .extracting(UserResponse::getUsername)
                .containsExactly("bob");
    }

    @Test
    @DisplayName("returns empty list when no users match keyword")
    void noMatches() {
        when(userRepo.findTop10ByUsernameContainingIgnoreCase("zzz")).thenReturn(List.of());
        assertThat(userService.searchUsers("zzz", "anyone")).isEmpty();
    }

    @Test
    @DisplayName("returns empty list when all results are the current user")
    void allResultsAreCurrentUser() {
        when(userRepo.findTop10ByUsernameContainingIgnoreCase("karthik"))
                .thenReturn(List.of(user("karthik")));
        assertThat(userService.searchUsers("karthik", "karthik")).isEmpty();
    }

    private User user(String username) {
        return User.builder().username(username).password("enc").provider(AuthProvider.LOCAL).build();
    }
}
