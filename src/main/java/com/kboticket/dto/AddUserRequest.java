package com.kboticket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddUserRequest {
    private String email;
    private String name;
    private String password;
}
