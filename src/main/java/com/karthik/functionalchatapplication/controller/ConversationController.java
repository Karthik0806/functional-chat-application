package com.karthik.functionalchatapplication.controller;

import com.karthik.functionalchatapplication.dto.ConversationResponse;
import com.karthik.functionalchatapplication.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Conversation APIs")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "Get user conversations", description = "Returns all conversations of authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(Principal principal) {
        return ResponseEntity.ok(conversationService.getUserConversations(principal.getName()));
    }
}