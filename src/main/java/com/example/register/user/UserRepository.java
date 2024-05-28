package com.example.register.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByUsername(String username);
    public User findByPassword(String password);
   // public User findByFirstname(String firstname);


}