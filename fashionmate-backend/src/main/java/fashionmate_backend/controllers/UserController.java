package fashionmate_backend.controllers;

import fashionmate_backend.models.User;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {


    @Autowired
    private UserRepository userRepository;


    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PostMapping("/signup")
    public Long signUp(@RequestBody User request) throws Exception {
        // Check for the new user's name, email, and password
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new Exception("Add the missing fields");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already in use");
        }
        final User user = userRepository.save(request);

        return user.getId();
    }

    @PostMapping("/signin")
    public Long signIn(@RequestBody User request) throws Exception {
        //Check for the user's email and password is null or not
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new Exception("Please enter your correct email and password");
        }

        final User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user != null) {
            return user.getId();
        } else {
            throw new RuntimeException("User not found");
        }
    }


}

