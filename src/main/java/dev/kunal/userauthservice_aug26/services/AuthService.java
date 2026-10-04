package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.exceptions.InvalidCredentialsException;
import dev.kunal.userauthservice_aug26.exceptions.UserAlreadyExistsException;
import dev.kunal.userauthservice_aug26.exceptions.UserNotFoundException;
import dev.kunal.userauthservice_aug26.models.Role;
import dev.kunal.userauthservice_aug26.models.User;
import dev.kunal.userauthservice_aug26.repositories.RoleRepository;
import dev.kunal.userauthservice_aug26.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User signup(String username, String email, String password) {

        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + email + " already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        Optional<Role> roleOptional = roleRepository.findByValue("USER");
        if (roleOptional.isEmpty()) {
            Role role = new Role();
            role.setValue("USER");
            roleRepository.save(role);
            user.setRole(List.of(role));
        } else {
            user.setRole(List.of(roleOptional.get()));
        }

        return userRepository.save(user);
    }

    @Override
    public User login(String email, String password) {

        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return user;
    }

    @Override
    public User makeAdmin(String email, String password) {

        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Optional<Role> roleOptional = roleRepository.findByValue("ADMIN");
        if (roleOptional.isEmpty()) {
            Role role = new Role();
            role.setValue("ADMIN");
            roleRepository.save(role);
        } else {
            List<Role> roles = user.getRole();
            roles.add(roleOptional.get());
            user.setRole(roles);
        }

        return userRepository.save(user);
    }
}
