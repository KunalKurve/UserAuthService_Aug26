package dev.kunal.userauthservice_aug26.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequestDto {

    private String username;
    private String email;
    private String password;

}
