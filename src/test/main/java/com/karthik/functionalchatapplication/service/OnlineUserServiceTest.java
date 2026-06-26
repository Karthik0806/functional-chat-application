package com.karthik.functionalchatapplication.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OnlineUserServiceTest {

    private OnlineUserService service;

    @BeforeEach
    void setUp() {
        service = new OnlineUserService();
    }

    @Test
    @DisplayName("addUser marks user as online")
    void addUser() {
        service.addUser("alice");
        assertThat(service.isOnline("alice")).isTrue();
    }

    @Test
    @DisplayName("removeUser marks user as offline")
    void removeUser() {
        service.addUser("alice");
        service.removeUser("alice");
        assertThat(service.isOnline("alice")).isFalse();
    }

    @Test
    @DisplayName("isOnline returns false for users never added")
    void unknownUser() {
        assertThat(service.isOnline("ghost")).isFalse();
    }

    @Test
    @DisplayName("getOnlineUsers returns an immutable snapshot of all online users")
    void getOnlineUsersSnapshot() {
        service.addUser("alice");
        service.addUser("bob");
        var users = service.getOnlineUsers();

        assertThat(users).containsExactlyInAnyOrder("alice", "bob");
        // snapshot is immutable
        assertThat(users).isUnmodifiable();
    }

    @Test
    @DisplayName("removing a user does not affect other online users")
    void removeDoesNotAffectOthers() {
        service.addUser("alice");
        service.addUser("bob");
        service.removeUser("alice");
        assertThat(service.isOnline("bob")).isTrue();
    }

    @Test
    @DisplayName("removeUser on non-existent user is a no-op")
    void removeNonExistentIsNoop() {
        service.removeUser("nobody");
        assertThat(service.getOnlineUsers()).isEmpty();
    }

    @Test
    @DisplayName("duplicate addUser does not create duplicates in set")
    void duplicateAdd() {
        service.addUser("alice");
        service.addUser("alice");
        assertThat(service.getOnlineUsers()).hasSize(1);
    }
}
