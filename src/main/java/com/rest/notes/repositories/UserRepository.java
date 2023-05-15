package com.rest.notes.repositories;

import java.util.Optional;

import com.rest.notes.models.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}