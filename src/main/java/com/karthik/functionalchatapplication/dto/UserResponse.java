package com.karthik.functionalchatapplication.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String username;
    private boolean online;
}
