package com.project.ChatApplication.Repository;

import com.project.ChatApplication.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UsersRepo extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
}
