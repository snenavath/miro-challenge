package com.rest.notes.controllers;

import com.rest.notes.models.User;
import com.rest.notes.repositories.UserRepository;
import com.rest.notes.services.UsersService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;


@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    private final Bucket bucket;

    public AuthController() {
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping("/login")
    public ResponseEntity<HttpStatus> login(@RequestBody User user) throws Exception {

        Authentication authObject = null;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authObject);
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid credentials");
        }

        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();
        if (username == null || password == null) {
            return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
        }
        if (username.length() < 1 || username.length() > 15) {
            return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
        }
        if (password.length() < 1 || password.length() > 15) {
            return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
        }

        try {
            userRepository.save(user);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<HttpStatus>(HttpStatus.OK);
    }

}

