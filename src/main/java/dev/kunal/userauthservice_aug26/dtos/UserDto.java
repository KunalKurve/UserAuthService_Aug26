package dev.kunal.userauthservice_aug26.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private List<String> roles;
}
