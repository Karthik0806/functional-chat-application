package com.karthik.functionalchatapplication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadReceipt {
    private String sender;
    private String receiver;
}
