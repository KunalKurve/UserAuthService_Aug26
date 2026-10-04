package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.models.User;
import org.antlr.v4.runtime.misc.Pair;

public interface IAuthService {

    User signup(String username, String email, String password);

    Pair<User, String> login(String email, String password);

    User makeAdmin(String email, String password);

    boolean validateToken(String token);
}
