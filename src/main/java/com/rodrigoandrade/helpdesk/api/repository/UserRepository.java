package com.rodrigoandrade.helpdesk.api.repository;

import com.rodrigoandrade.helpdesk.api.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    User findByEmail(String email);

}
