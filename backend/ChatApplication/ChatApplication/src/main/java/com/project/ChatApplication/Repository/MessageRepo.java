package com.project.ChatApplication.Repository;

import com.project.ChatApplication.Model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface MessageRepo extends MongoRepository<Message,String>{

}
