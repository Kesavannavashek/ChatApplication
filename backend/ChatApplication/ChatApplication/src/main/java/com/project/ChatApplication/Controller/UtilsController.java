package com.project.ChatApplication.Controller;

import com.project.ChatApplication.Model.User;
import com.project.ChatApplication.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/")
public class UtilsController {

    @Autowired
    private UsersRepo usersRepo;

    @PutMapping("/makeOnline")
    public ResponseEntity<String> makeOnline(@RequestParam String email){
        Optional<User> userOptional = usersRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            return ResponseEntity.badRequest().body("No Email Found...");
        }
        User user = userOptional.get();
        user.setOnline(true);
        usersRepo.save(user);
        return ResponseEntity.ok("Updated Successfully");
    }

}
