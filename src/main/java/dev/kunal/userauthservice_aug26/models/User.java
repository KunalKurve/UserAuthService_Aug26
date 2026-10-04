package dev.kunal.userauthservice_aug26.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
public class User extends BaseModel {

    // attributes for Authentication
    private String username;

    private String email;

    private String password;

    private String phoneNumber;

    // attributes for Authorization

    @ManyToMany
    private List<Role> role;
}
