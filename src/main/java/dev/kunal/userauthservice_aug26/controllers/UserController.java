package dev.kunal.userauthservice_aug26.controllers;

import dev.kunal.userauthservice_aug26.dtos.UserDto;
import dev.kunal.userauthservice_aug26.models.Role;
import dev.kunal.userauthservice_aug26.models.User;
import dev.kunal.userauthservice_aug26.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    public UserDto getUserDetailsById(@PathVariable Long id) {
        User user = userService.getUserDetailsById(id);
        if(user == null) {
            return null;
        }
        return convertToUserDto(user);
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
