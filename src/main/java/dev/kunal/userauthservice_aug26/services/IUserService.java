package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.models.User;

public interface IUserService {

    User getUserDetailsById(Long id);
}
