package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.models.User;

public interface IAuthService {

    User signup(String username, String email, String password);

    User login(String email, String password);

    User makeAdmin(String email, String password);
}
