package com.project.ChatApplication.Service;

import com.project.ChatApplication.Model.Message;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

public void save(Message message);


}
