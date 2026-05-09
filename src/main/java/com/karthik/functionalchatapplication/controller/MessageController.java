package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.entity.Message;
import com.karthik.functionalchatapplication.service.ChatService;
import com.karthik.functionalchatapplication.service.OnlineUserService;
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
@RequestMapping("/messages")
@Tag(name = "Message APIs")
public class MessageController {
    private final ChatService service;
    private final OnlineUserService onlineUserService;

    @Operation(summary = "Get chat history", description = "Returns conversation messages between authenticated user and receiver")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @GetMapping("/history")
    public ResponseEntity<List<Message>> getMessages(@RequestParam String receiver, Principal principal) {
        return ResponseEntity.ok(service.getChatMessages(principal.getName(), receiver));
    }



    @Operation(summary = "Get online users", description = "Returns currently online users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Online users fetched successfully")})
    @GetMapping("/online-users")
    public ResponseEntity<Set<String>> onlineUsers() {
        return ResponseEntity.ok(onlineUserService.getOnlineUsers());
    }
}