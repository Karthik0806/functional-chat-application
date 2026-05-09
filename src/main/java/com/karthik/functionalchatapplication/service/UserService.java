package com.karthik.functionalchatapplication.service;

import com.karthik.functionalchatapplication.dto.UserResponse;
import com.karthik.functionalchatapplication.entity.User;
import com.karthik.functionalchatapplication.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final OnlineUserService onlineUserService;

    public List<UserResponse> searchUsers(String keyword, String currentUser) {

        List<User> users = userRepo.findTop10ByUsernameContainingIgnoreCase(keyword);
        return users.stream().filter(user -> !user.getUsername().equals(currentUser)).map(user ->
                        UserResponse.builder()
                                .username(user.getUsername())
                                .online(onlineUserService.isOnline(user.getUsername()))
                                .build()).toList();
    }
}