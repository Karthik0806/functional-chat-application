package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.dto.UserResponse;
import com.karthik.functionalchatapplication.service.OnlineUserService;
import com.karthik.functionalchatapplication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User APIs")
public class UserController {
    private final OnlineUserService onlineUserService;
    private final UserService userService;

    @Operation(summary = "Get online users", description = "Returns all users currently online")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Online users fetched successfully")})
    @GetMapping("/online")
    public ResponseEntity<Set<String>> getOnlineUsers(){
        return ResponseEntity.ok(onlineUserService.getOnlineUsers());
    }

    @Operation(summary = "Search users", description = "Searches users by username excluding authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword, Principal principal) {
        return ResponseEntity.ok(userService.searchUsers(keyword, principal.getName()));
    }
}