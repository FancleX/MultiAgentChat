package com.neu.server.restServer.service;

import com.neu.encryption.Encryption;
import com.neu.server.restServer.repository.UserRepository;
import com.neu.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> signup(Map<String, String> data) {
        // parse the map
        // get email
        String email = data.get("email");
        // get password
        String password = data.get("password");
        // get nickname
        String nickname = data.get("nickname");

        // look up for the email if it is already registered
        Long id = userRepository.getUserIdByEmail(email);
        if (id != null) {
            return ResponseEntity.badRequest().body("The email has been registered");
        }
        // accept the registry
        // generate salt for the user
        String salt = Encryption.saltGenerater();
        // encrypt the password
        String encryptedPassword = Encryption.md5(password, salt);
        // create an account
        User user = new User(nickname, email, encryptedPassword, salt);
        // save the entity
        userRepository.save(user);
        return ResponseEntity.ok("Welcome to join us");
    }

    public ResponseEntity<Map<String, Object>> login(Map<String, Object> data) {
        // parse map
        String email = (String) data.get("email");
        String password = (String) data.get("password");
        String ip = (String) data.get("ip");
        int port = (int) data.get("port");

        Map<String, Object> response = new HashMap<>();
        // check if the user exists
        Long id = userRepository.getUserIdByEmail(email);
        if (id == null) {
            response.put("error", "Account doesn't exist");
            return ResponseEntity.badRequest().body(response);
        }
        // check password
        User user = userRepository.findById(id).get();
        String salt = user.getSalt();
        String givenPassword = Encryption.md5(password, salt);
        String correctPassword = user.getPassword();
        // incorrect password
        if (!correctPassword.equals(givenPassword)) {
            response.put("error", "Incorrect password");
            return ResponseEntity.badRequest().body(response);
        }

        // record the ip and port of this time login
        // db
        userRepository.updateIpAndPort(id, ip, port);

        // return the leader ip and port of the p to p network

        response.put("ip", "");
        response.put("port", 1);

        return ResponseEntity.ok(response);
    }

}
