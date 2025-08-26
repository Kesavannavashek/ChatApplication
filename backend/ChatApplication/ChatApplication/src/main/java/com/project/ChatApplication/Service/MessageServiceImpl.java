package com.project.ChatApplication.Service;
import com.project.ChatApplication.Model.Message;
import com.project.ChatApplication.Repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    public MessageRepo messageRepo;

    @Override
    public void save(Message message) {
        messageRepo.save(message);
    }
}
