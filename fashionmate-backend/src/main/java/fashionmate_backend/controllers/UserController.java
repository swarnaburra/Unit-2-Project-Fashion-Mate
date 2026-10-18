package fashionmate_backend.controllers;

import fashionmate_backend.exceptions.EmailAlreadyInUseException;
import fashionmate_backend.exceptions.InvalidCredentialsException;
import fashionmate_backend.exceptions.MissingFieldsException;
import fashionmate_backend.models.User;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Manages {@link User} creation, sign-up, and sign-in.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {


    @Autowired
    private UserRepository userRepository;


    /**
     * Handles {@code POST /api/users}.
     * <p>
     * Saves the given user as-is, without any validation of its fields
     * or uniqueness checks.
     *
     * @param user the user payload from the request body
     * @return the saved {@link User}, including its generated id
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * Handles {@code POST /api/users/signup}.
     * <p>
     * Validates that the request has a name, email, and password, ensures the
     * email is not already registered, then creates the new user.
     *
     * @param request the new user's details from the request body
     * @return the id of the newly created user
     * @throws MissingFieldsException if the name, email, or password is missing (400)
     * @throws EmailAlreadyInUseException if the email is already registered (409)
     */
    @PostMapping("/signup")
    public Long signUp(@RequestBody User request) {
        // Check for the new user's name, email, and password
        if (request.getName() == null || request.getEmail() == null || request.getPassword() == null) {
            throw new MissingFieldsException("Add the missing fields");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyInUseException("Email already in use");
        }
        final User user = userRepository.save(request);

        return user.getId();
    }

    /**
     * Handles {@code PUT /api/users/signin}.
     * <p>
     * Validates that the request has an email and password, then looks up
     * a matching user by that email/password pair.
     *
     * @param request the login credentials (email and password) from the request body
     * @return the id of the matching user
     * @throws MissingFieldsException if the email or password is missing (400)
     * @throws InvalidCredentialsException if no user matches the given email/password combination (401)
     */
    @PutMapping("/signin")
    public Long signIn(@RequestBody User request) {
        //Check for the user's email and password is null or not
        if (request.getEmail() == null || request.getPassword() == null) {
            throw new MissingFieldsException("Please enter your correct email and password");
        }

        final User user = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (user != null) {
            return user.getId();
        } else {
            throw new InvalidCredentialsException("User not found");
        }
    }


}

