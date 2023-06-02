package com.newgate.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserToken {
    private String team;
    private String branchCode;
    private String username;
    private String regionCode;
    private List<String> roles;
    private String userCode;
    private String name;
    private String sessionState;
    private String token;
}
