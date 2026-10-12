package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.models.User;
import dev.kunal.userauthservice_aug26.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserDetailsById(Long id) {

        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);

    }
}
