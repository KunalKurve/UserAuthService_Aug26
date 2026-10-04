package dev.kunal.userauthservice_aug26.controllers;


import dev.kunal.userauthservice_aug26.dtos.LoginRequestDto;
import dev.kunal.userauthservice_aug26.dtos.SignupRequestDto;
import dev.kunal.userauthservice_aug26.dtos.UserDto;
import dev.kunal.userauthservice_aug26.dtos.ValidateTokenRequestDto;
import dev.kunal.userauthservice_aug26.models.Role;
import dev.kunal.userauthservice_aug26.models.User;
import dev.kunal.userauthservice_aug26.services.IAuthService;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {

        User user = authService.signup(
                signupRequestDto.getUsername(),
                signupRequestDto.getEmail(),
                signupRequestDto.getPassword()
        );
        return new ResponseEntity<>(convertToUserDto(user), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {

        Pair<User, String> response = authService.login(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );
        User user = response.a;
        String token = response.b;
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE, "auth_token=" + token);

        return new ResponseEntity<>(convertToUserDto(user), headers, HttpStatus.ACCEPTED);
    }

    @PostMapping("/make-Admin")
    public ResponseEntity<UserDto> makeAdmin(@RequestBody LoginRequestDto loginRequestDto) {

        User user = authService.makeAdmin(
                loginRequestDto.getEmail(),
                loginRequestDto.getPassword()
        );

        return new ResponseEntity<>(convertToUserDto(user), HttpStatus.ACCEPTED);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Boolean> validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {

        boolean isValid = authService.validateToken(validateTokenRequestDto.getToken());
        return new ResponseEntity<>(isValid, HttpStatus.OK);
    }


    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRole().stream().map(Role::getValue).toList());
        return userDto;
    }


}
