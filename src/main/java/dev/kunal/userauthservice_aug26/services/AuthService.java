package dev.kunal.userauthservice_aug26.services;

import dev.kunal.userauthservice_aug26.exceptions.InvalidCredentialsException;
import dev.kunal.userauthservice_aug26.exceptions.UserAlreadyExistsException;
import dev.kunal.userauthservice_aug26.exceptions.UserNotFoundException;
import dev.kunal.userauthservice_aug26.models.Role;
import dev.kunal.userauthservice_aug26.models.User;
import dev.kunal.userauthservice_aug26.models.UserSession;
import dev.kunal.userauthservice_aug26.models.enums.Status;
import dev.kunal.userauthservice_aug26.repositories.RoleRepository;
import dev.kunal.userauthservice_aug26.repositories.SessionRepo;
import dev.kunal.userauthservice_aug26.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecretKey secretKey;

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
    public Pair<User, String> login(String email, String password) {

        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        //Generate JWT token and set it to the user object
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("issuer", "Scaler");

        long currentTime = System.currentTimeMillis();
        claims.put("iat", currentTime);
        claims.put("exp", currentTime + 10000); // 10 seconds expiry for testing purpose, can be increased to 1 hour or more
        List<String> roles = new ArrayList<>();
        for(Role role : user.getRole()) {
            roles.add(role.getValue());
        }
        claims.put("access", roles);

        // removed this code to make it accessible from AuthConfig.java and autowired here
        // MacAlgorithm algorithm = Jwts.SIG.HS256;
        // SecretKey secretKey = algorithm.key().build();
        // secretKey is built on Top of Algorithm, therefore I don't need to pass algorithm in token generation
        // String token = Jwts.builder().claims(claims).compact(); // just token having a payload, no signature

        // Use the following line when the SecretKey was generated separately and not built on top of Algorithm
        // String token = Jwts.builder().claims(claims).signWith(secretKey, algorithm).compact();

        String token = Jwts.builder().claims(claims).signWith(secretKey).compact(); // token having a payload and signature

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setToken(token);
        sessionRepo.save(userSession);

        return new Pair<>(user, token);
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

    @Override
    public boolean validateToken(String token /*, Long userId */) {

        // can add exception in case the secretkey is changed and the token is not valid anymore,
        // but for now just return false

        // can add check for userId as well,
        // in case someone tries to steal the token and use it for another user, but for now just return false

        // check if the token was created by us or not
        Optional<UserSession> userSessionOptional = sessionRepo.findByToken(token);
        if (userSessionOptional.isEmpty()) {
            return false;
        }

        // check expiry
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        long exp = claims.get("exp", Long.class);
        long currentTime = System.currentTimeMillis();
        System.out.println("Current time: " + currentTime);
        System.out.println("Token expiry time: " + exp);
        if (currentTime > exp) {
            UserSession userSession = userSessionOptional.get();
            userSession.setStatus(Status.INACTIVE);
            sessionRepo.save(userSession);
            System.out.println("Token expired for user: " + userSession.getUser().getEmail());
            // or can delete the session from the database
            // sessionRepo.deleteById(userSession.getId());
            return false;
        }

        return true;
    }
}
