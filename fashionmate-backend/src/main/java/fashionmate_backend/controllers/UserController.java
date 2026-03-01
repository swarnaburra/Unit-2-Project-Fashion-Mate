package fashionmate_backend.controllers;

import fashionmate_backend.models.User;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public boolean signUp(@RequestBody User request) throws Exception {
        // Check for the new user's name, email, and password
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new Exception("Add the missing fields");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already in use");
        }
        userRepository.save(request);

        return true;
    }

    @PostMapping("/signin")
    public boolean signIn(@RequestBody User request) throws Exception {
        //Check for the user's email and password is null or not
        if (request.getEmail() == null || request.getPassword() == null){
            throw new Exception("Please enter your correct email and password");
        }

        if (userRepository.existsByEmailAndPassword(request.getEmail(), request.getPassword())){
            return true;
       }
        return true;
    }

}

